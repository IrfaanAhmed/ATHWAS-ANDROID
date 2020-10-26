package com.app.ia.ui.product_list

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.model.ProductListingResponse
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.common_header.view.*


class ProductListActivity : BaseActivity<ActivityProductListBinding, ProductListViewModel>() {

    private var mBinding: ActivityProductListBinding? = null
    private var mViewModel: ProductListViewModel? = null
    private lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var productAdapter: ProductListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_list
    }

    override fun getViewModel(): ProductListViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.visible()
        toolbar.ivSearchIcon.visible()
        toolbar.ivEditProfileIcon.gone()

        toolbar.imageViewIcon.setOnClickListener {
            //startActivity<MyCartActivity>()
        }

        toolbar.ivSearchIcon.setOnClickListener {
            startActivity<SearchActivity>()
        }

        recViewProduct.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewProduct.addItemDecoration(DividerItemDecoration(this@ProductListActivity, LinearLayout.VERTICAL))
        productAdapter = ProductListAdapter()
        productAdapter?.setOnItemClickListener(object : ProductListAdapter.OnItemClickListener {
            override fun onItemClick(productItem: ProductListingResponse.Docs) {
                startActivity<ProductDetailActivity> {
                    putExtra("product_id", productItem.Id)
                }
            }

            override fun onFavoriteClick(productItem: ProductListingResponse.Docs, position: Int) {
                mViewModel?.favPosition?.value = position
                mViewModel?.addFavorite(productItem.Id, if (productItem.isFavourite == 0) 1 else 0)
            }
        })

        recViewProduct.adapter = productAdapter

        recyclerViewPaging = object : RecyclerViewPaginator(recViewProduct) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.setUpObserver()
            }
        }

        recViewProduct.addOnScrollListener(recyclerViewPaging)

        mViewModel?.productList?.observe(this, {

            if (it.size <= 0) {
                productAdapter?.notifyDataSetChanged()
            } else {
                if (productAdapter?.currentList?.size!! == 0) {
                    productAdapter?.submitList(it)
                } else {
                    productAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductListViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductListViewModel::class.java)
    }
}