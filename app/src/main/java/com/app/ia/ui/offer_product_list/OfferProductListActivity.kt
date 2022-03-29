package com.app.ia.ui.offer_product_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityOfferProductListBinding
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.OffersResponse
import com.app.ia.model.ProductListingResponse
import com.app.ia.ui.my_cart.MyCartActivity
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.product_list.ProductListViewModel
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.ui.search.SearchActivity
import com.app.ia.ui.search.SearchViewModel
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.common_header.view.*

class OfferProductListActivity : BaseActivity<ActivityOfferProductListBinding, OfferProductListViewModel>() {

    lateinit var mBinding: ActivityOfferProductListBinding
    lateinit var mViewModel: OfferProductListViewModel
    //lateinit var recyclerViewPaging: RecyclerViewPaginator
    private var productAdapter: OfferProductListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_offer_product_list
    }

    override fun getViewModel(): OfferProductListViewModel {
        return mViewModel!!
    }

    val offerData: OffersResponse.Docs? by lazy {
        if(intent.hasExtra(OFFER_DATA))
            intent.getSerializableExtra(OFFER_DATA) as OffersResponse.Docs
        else null
    }

    companion object{
        const val OFFER_DATA = "OFFER_DATA"
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
            if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
                loginDialog()
            } else {
                startActivity<MyCartActivity>()
            }
        }

        toolbar.ivSearchIcon.setOnClickListener {
            startActivity<SearchActivity>(){
                putExtra(SearchViewModel.BUSINESS_CATEGORY_ID, mViewModel?.businessCategoryId?.value)
                putExtra(SearchViewModel.CATEGORY_ID, mViewModel?.categoryId?.value)
                putExtra(SearchViewModel.SUB_CATEGORY_ID, mViewModel?.subCategoryId?.value)
            }
        }

        mSwipeRefresh.isEnabled = false

        mSwipeRefresh.setOnRefreshListener {
            if (mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = false
            }
            mViewModel?.currentPage?.value = 1
            mViewModel?.productListAll?.clear()
            //recyclerViewPaging.reset()
            //resetPaginationOnFilterSet()

            when (mViewModel?.type) {
                0 -> {
                    mViewModel?.setUpObserver()
                }
                3 -> {
                    mViewModel?.dealOfTheDayBannerProductObserver(mViewModel?.bannerId?.value!!)
                }
                else -> {
                    mViewModel?.popularDiscountedProductObserver()
                }
            }
        }

        recViewProduct.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewProduct.addItemDecoration(DividerItemDecoration(this@OfferProductListActivity, LinearLayout.VERTICAL))

        productAdapter = OfferProductListAdapter()
        productAdapter?.setOnItemClickListener(object : OfferProductListAdapter.OnItemClickListener {
            override fun onItemClick(productItem: OffersResponse.Product) {
                mStartActivityForResult<ProductDetailActivity>(6677) {
                    putExtra("product_id", productItem.Id)
                }
            }

            override fun onFavoriteClick(productItem: OffersResponse.Product, position: Int) {
                if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
                    loginDialog()
                } else {
                    mViewModel?.favPosition?.value = position
                    mViewModel?.addFavorite(productItem.Id, if (productItem.productData[0].isFavourite == 0) 1 else 0)
                }
            }

            override fun onAddToCartClick(productItem: OffersResponse.Product) {

                if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
                    loginDialog()
                } else {
                    offerData?.let {
                        val requestParams = HashMap<String, String>()
                        requestParams["inventory_id"] = productItem.Id
                        requestParams["product_id"] = productItem.productId
                        requestParams["business_category_id"] = it.businessCategory[0].Id
                        mViewModel?.addItemToCartObserver(requestParams)
                    }

                    /*if (productItem.availableQuantity < 1) {
                        requestParams["product_id"] = productItem.Id
                        mViewModel?.notifyMeObserver(requestParams)
                    } else {
                        requestParams["inventory_id"] = productItem.Id
                        requestParams["product_id"] = productItem.mainProductId
                        requestParams["business_category_id"] = productItem.businessCategory.Id
                        mViewModel?.addItemToCartObserver(requestParams)
                    }*/
                }
            }
        })

        recViewProduct.adapter = productAdapter

        //resetPaginationOnFilterSet()




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



        offerData?.product?.let {
            mViewModel?.productList.value = it.toMutableList()
            Log.e("List", "${it.size}")
        }
    }

    override fun onStart() {
        super.onStart()
        //mViewModel?.onStart()
    }

    override fun onResume() {
        super.onResume()
        CommonUtils.showCartItemCount(toolbar.bottom_navigation_notification)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OfferProductListViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OfferProductListViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6677 && resultCode == Activity.RESULT_OK) {

            val favList = data?.getSerializableExtra("favItems") as HashMap<String, Boolean>

            for (items in mViewModel?.productList?.value!!) {
                if (favList[items.Id] != null) {
                   // items.isFavourite = if (favList[items.Id]!!) 1 else 0
                }
            }
            productAdapter?.notifyDataSetChanged()

        } else if (supportFragmentManager.findFragmentByTag("filter_dialog") != null) {
            val addressDialog = supportFragmentManager.findFragmentByTag("filter_dialog") as ProductFilterDialogFragment
            addressDialog.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun resetPaginationOnFilterSet() {

        /*recyclerViewPaging = object : RecyclerViewPaginator(recViewProduct) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                if(mViewModel?.currentPage?.value!! == start) return

                mViewModel?.currentPage?.value = mViewModel?.currentPage?.value!! + 1
                if (mViewModel?.type == 0) {
                    mViewModel?.setUpObserver()
                } else {
                    mViewModel?.popularDiscountedProductObserver()
                }
            }
        }
        recViewProduct.addOnScrollListener(recyclerViewPaging)*/
    }

}