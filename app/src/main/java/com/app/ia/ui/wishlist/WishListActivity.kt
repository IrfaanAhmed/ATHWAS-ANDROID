package com.app.ia.ui.wishlist

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
import com.app.ia.databinding.ActivityWishListBinding
import com.app.ia.model.FavoriteListResponse
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.search.SearchActivity
import com.app.ia.ui.wishlist.adapter.WishListAdapter
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_wish_list.*
import kotlinx.android.synthetic.main.common_header.view.*

class WishListActivity : BaseActivity<ActivityWishListBinding, WishListViewModel>() {

    private var mBinding: ActivityWishListBinding? = null
    private var mViewModel: WishListViewModel? = null
    private lateinit var recyclerViewPaging: RecyclerViewPaginator
    var wishListAdapter: WishListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_wish_list
    }

    override fun getViewModel(): WishListViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.gone()
        toolbar.ivSearchIcon.gone()
        toolbar.ivEditProfileIcon.gone()

        toolbar.imageViewIcon.setOnClickListener {
            //startActivity<MyCartActivity>()
        }

        toolbar.ivSearchIcon.setOnClickListener {
            startActivity<SearchActivity>()
        }

        recViewWishList.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewWishList.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        wishListAdapter = WishListAdapter()
        wishListAdapter?.setOnItemClickListener(object : WishListAdapter.OnItemClickListener {

            override fun onFavoriteClick(productItem: FavoriteListResponse.Docs, position: Int) {
                mViewModel?.addFavorite(productItem.inventoryId, position)
            }

            override fun onItemClick(productItem: FavoriteListResponse.Docs, position: Int) {
                startActivity<ProductDetailActivity> {
                    putExtra("product_id", productItem.inventoryId)
                }
            }

            override fun onAddToCartClick(productItem: FavoriteListResponse.Docs) {
                val requestParams = HashMap<String, String>()
                if (productItem.quantity < 1) {
                    requestParams["product_id"] = productItem.inventoryId
                    mViewModel?.notifyMeObserver(requestParams)
                } else {
                    requestParams["inventory_id"] = productItem.inventoryId
                    requestParams["product_id"] = productItem.productId
                    requestParams["business_category_id"] = productItem.businessCategory.Id
                    mViewModel?.addItemToCartObserver(requestParams)
                }
            }
        })

        recViewWishList.adapter = wishListAdapter

        recyclerViewPaging = object : RecyclerViewPaginator(recViewWishList) {
            override val isLastPage: Boolean
                get() = mViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mViewModel?.currentPage?.value = start
                mViewModel?.setUpObserver()
            }
        }

        recViewWishList.addOnScrollListener(recyclerViewPaging)

        mViewModel?.favoriteList?.observe(this, {

            if (it.size <= 0) {
                wishListAdapter?.notifyDataSetChanged()
            } else {
                if (wishListAdapter?.currentList?.size!! == 0) {
                    wishListAdapter?.submitList(it)
                } else {
                    wishListAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(WishListViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(WishListViewModel::class.java)
    }
}