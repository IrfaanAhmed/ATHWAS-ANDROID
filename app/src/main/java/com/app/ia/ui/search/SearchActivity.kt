package com.app.ia.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivitySearchBinding
import com.app.ia.ui.search.adapter.SearchAdapter
import com.app.ia.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.common_header.view.*
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {

    lateinit var mBinding: ActivitySearchBinding
    lateinit var mViewModel: SearchViewModel

    //private lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var searchAdapter: SearchAdapter? = null
    var keyword = ""

    val subject = PublishSubject.create<String>()
    private var disposable: Disposable? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun getViewModel(): SearchViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)

        mBinding = getViewDataBinding()
        mBinding.activity = this
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.imageViewIcon.invisible()
        toolbar.imageViewBack.invisible()

        searchAdapter = SearchAdapter()
        /*searchAdapter?.setOnItemClickListener(object : ProductListAdapter.OnItemClickListener {
            override fun onItemClick(productItem: ProductListingResponse.Docs) {
                startActivity<ProductDetailActivity>()
            }

            override fun onFavoriteClick(productItem: ProductListingResponse.Docs, position: Int) {
                mViewModel?.favPosition?.value = position
                mViewModel?.addFavorite(productItem.Id, if (productItem.isFavourite == 0) 1 else 0)
            }
        })*/

        recViewSearchProduct.adapter = searchAdapter

       /* recyclerViewPaging = object : RecyclerViewPaginator(recViewSearchProduct) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.setUpObserver(keyword)
            }
        }

        recViewSearchProduct.addOnScrollListener(recyclerViewPaging)*/

        mBinding?.recViewSearchProduct?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy)
                Log.d("Scrolled", "Scrolled")
                val totalItemCount = (mBinding?.recViewSearchProduct.layoutManager as LinearLayoutManager).itemCount
                val lastVisibleItem = (mBinding?.recViewSearchProduct.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Log.d("Scrolled", "Scrolled ${totalItemCount} $lastVisibleItem")
                if (!mViewModel?.isLoading && mViewModel.isLastPage.value == false && totalItemCount == (lastVisibleItem + 1)) {
                    mViewModel.isLoading = true
                    mViewModel?.currentPage?.value = mViewModel?.currentPage?.value!! + 1
                    //page++
                    mViewModel?.setUpObserver(keyword)
                }
            }
        })

        mViewModel?.productList?.observe(this, {

            if (it.size <= 0) {
                searchAdapter?.notifyDataSetChanged()
            } else {
                if (searchAdapter?.currentList?.size!! == 0) {
                    searchAdapter?.submitList(it)
                } else {
                    searchAdapter?.notifyDataSetChanged()
                }
            }
        })

        edtSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charater: CharSequence?, p1: Int, p2: Int, p3: Int) {
                keyword = charater.toString()
                mViewModel?.isSearchTextEntered!!.value = keyword.isNotEmpty()
                if (keyword.length > 2) {
                    subject.onNext(keyword)
                } else if (keyword.isEmpty()) {
                    mViewModel?.productListAll?.clear()
                    mViewModel?.productList?.value = ArrayList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        disposable = subject.debounce(300, TimeUnit.MILLISECONDS)
            .filter { text ->
                text.isNotEmpty()
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mViewModel?.productListAll?.clear()
                mViewModel?.setUpObserver(it)
            }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SearchViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }

     fun startSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.what_are_you_looking_for))
        startActivityForResult(intent, AppRequestCode.REQUEST_INSTRUCTION_SPEECH_RECOGNIZER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_INSTRUCTION_SPEECH_RECOGNIZER) {
            if (resultCode == Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                startActivityWithFinish<SearchActivity> {
                    putExtra(AppConstants.EXTRA_VOICE_TEXT, results!![0])
                }
            }
        }
    }
}