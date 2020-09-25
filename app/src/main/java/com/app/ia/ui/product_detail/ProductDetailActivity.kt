package com.app.ia.ui.product_detail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.ui.product_detail.adapter.ProductImageAdapter
import com.app.ia.ui.product_detail.adapter.SimilarProductListAdapter
import com.app.ia.utils.gone
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.visible
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.toolbar
import kotlinx.android.synthetic.main.common_header.view.*

class ProductDetailActivity : BaseActivity<ActivityProductDetailBinding, ProductDetailViewModel>() {

    private var mBinding: ActivityProductDetailBinding? = null
    private var mViewModel: ProductDetailViewModel? = null

    var similarProductAdapter: SimilarProductListAdapter? = null

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
        mViewModel?.setVariable(mBinding!!)
        //mViewModel?.setIntent(intent)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.visible()
        toolbar.ivSearchIcon.visible()
        toolbar.ivEditProfileIcon.gone()

        val imageList = ArrayList<String>()
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61rDJVoKpeL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/619-6lqjiWL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61CCg6TPCkL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61f5taWUTnL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61hMY2fhtVL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61-74EQUnVL._SL1000_.jpg")
        imageList.add("https://images-na.ssl-images-amazon.com/images/I/61WJkNTymYL._SL1000_.jpg")

        val bannerPagerAdapter = ProductImageAdapter(this@ProductDetailActivity, imageList)
        viewPagerBanner.adapter = bannerPagerAdapter
        viewPagerIndicator.setViewPager(viewPagerBanner)

        recViewSimilarProduct.layoutManager = LinearLayoutManager(this@ProductDetailActivity, RecyclerView.HORIZONTAL, false)
        similarProductAdapter = SimilarProductListAdapter()
        recViewSimilarProduct.adapter = similarProductAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        categoryList.add("iPhone")
        categoryList.add("Motorola")
        categoryList.add("RealMe")
        categoryList.add("MI")
        similarProductAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductDetailViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)
    }
}