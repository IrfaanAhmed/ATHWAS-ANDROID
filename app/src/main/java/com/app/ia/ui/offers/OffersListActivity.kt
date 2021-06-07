package com.app.ia.ui.offers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityOfferListBinding
import com.app.ia.model.OffersResponse
import com.app.ia.ui.offers.adapter.OfferListAdapter
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_offer_list.*
import kotlinx.android.synthetic.main.common_header.view.*
import java.util.concurrent.TimeUnit

class OffersListActivity : BaseActivity<ActivityOfferListBinding, OffersListViewModel>() {

    private var mActivityOffersBinding: ActivityOfferListBinding? = null
    private var mOffersViewModel: OffersListViewModel? = null
    private lateinit var recyclerViewPaging: RecyclerViewPaginator
    val subject = PublishSubject.create<String>()
    private var disposable: Disposable? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_offer_list
    }

    override fun getViewModel(): OffersListViewModel {
        return mOffersViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityOffersBinding = getViewDataBinding()
        mActivityOffersBinding?.lifecycleOwner = this
        mOffersViewModel?.setActivityNavigator(this)
        mOffersViewModel?.setVariable(mActivityOffersBinding!!, intent)

        setOnApplyWindowInset(toolbar, content_container)

        toolbar.imageViewIcon.invisible()

        recyclerViewOffers.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        val promoCodeAdapter = OfferListAdapter()
        recyclerViewOffers.adapter = promoCodeAdapter

        promoCodeAdapter.setOnItemSelectListener(object : OfferListAdapter.OnItemSelectListener {
            override fun onItemSelect(data: OffersResponse.Docs) {
                if(intent.getIntExtra("offerType", 1) == 3) {
                    startActivity<ProductDetailActivity> {
                        putExtra("product_id", data.productId[0])
                    }
                }
            }
        })

        recyclerViewPaging = object : RecyclerViewPaginator(recyclerViewOffers) {
            override val isLastPage: Boolean
                get() = mOffersViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mOffersViewModel?.currentPage?.value = start
                mOffersViewModel?.offerListObserver("")
            }
        }

        recyclerViewOffers.addOnScrollListener(recyclerViewPaging)

        mOffersViewModel?.promoCodeListData?.observe(this, {
            if (promoCodeAdapter.currentList.size == 0) {
                promoCodeAdapter.submitList(it)
            } else {
                promoCodeAdapter.notifyDataSetChanged()
            }
        })

        mOffersViewModel?.offerListObserver("")

        edtTextOffers.filters = arrayOf(CommonUtils.getEditTextFilter())

        edtTextOffers.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                subject.onNext(p0.toString())
            }
        })

        disposable = subject.debounce(300, TimeUnit.MILLISECONDS)
            /*.filter { text ->
                text.isNotEmpty()
            }*/
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mOffersViewModel?.promoCodeList?.clear()
                mOffersViewModel?.offerListObserver(it)
            }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OffersListViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mOffersViewModel = ViewModelProvider(this, factory).get(OffersListViewModel::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        disposable?.dispose()
    }
}