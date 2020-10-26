package com.app.ia.ui.search

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
import com.app.ia.databinding.ActivitySearchBinding
import com.app.ia.model.ProductListingResponse
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.ui.search.adapter.SearchAdapter
import com.app.ia.utils.RecyclerViewPaginator
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.common_header.view.*

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {

    private var mBinding: ActivitySearchBinding? = null
    private var mViewModel: SearchViewModel? = null

    private lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var searchAdapter: SearchAdapter? = null
    private var keyword = ""

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
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        //mViewModel?.setIntent(intent)

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

        recyclerViewPaging = object : RecyclerViewPaginator(recViewSearchProduct) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.setUpObserver(keyword)
            }
        }

        recViewSearchProduct.addOnScrollListener(recyclerViewPaging)

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
                    mViewModel?.setUpObserver(keyword)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SearchViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }
}