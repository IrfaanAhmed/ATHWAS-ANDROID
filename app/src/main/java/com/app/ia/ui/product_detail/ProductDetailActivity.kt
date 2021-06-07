package com.app.ia.ui.product_detail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.my_cart.MyCartActivity
import com.app.ia.ui.product_detail.adapter.ProductImageAdapter
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.content_container
import kotlinx.android.synthetic.main.activity_product_detail.toolbar
import kotlinx.android.synthetic.main.common_header.view.*

class ProductDetailActivity : BaseActivity<ActivityProductDetailBinding, ProductDetailViewModel>() {

    private var mBinding: ActivityProductDetailBinding? = null
    private var mViewModel: ProductDetailViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_detail
    }

    override fun getViewModel(): ProductDetailViewModel {
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
            if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
                loginDialog()
            } else {
                startActivity<MyCartActivity>()
            }
        }

        toolbar.ivSearchIcon.setOnClickListener {
            startActivity<SearchActivity>()
        }

        mViewModel?.productDetail?.observe(this, {
            val bannerPagerAdapter = ProductImageAdapter(this@ProductDetailActivity, it?.images)
            viewPagerBanner.adapter = bannerPagerAdapter
            viewPagerIndicator.setViewPager(viewPagerBanner)
        })

        priceTextView.paintFlags = priceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    override fun onResume() {
        super.onResume()
        CommonUtils.showCartItemCount(toolbar.bottom_navigation_notification)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductDetailViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("favItems", mViewModel?.favouriteChangedList)
        setResult(RESULT_OK, intent)
        finish()
    }
}