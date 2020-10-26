package com.app.ia.ui.product_detail

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
import com.app.ia.ui.product_detail.adapter.ProductImageAdapter
import com.app.ia.utils.gone
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.visible
import kotlinx.android.synthetic.main.activity_product_detail.*
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
            //startActivity<MyCartActivity>()
        }


        mViewModel?.productDetail?.observe(this, {
            val bannerPagerAdapter = ProductImageAdapter(this@ProductDetailActivity, it?.images)
            viewPagerBanner.adapter = bannerPagerAdapter
            viewPagerIndicator.setViewPager(viewPagerBanner)
        })

        priceTextView.paintFlags = priceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductDetailViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)
    }
}