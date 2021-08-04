package com.app.ia.ui.product_detail

import android.content.Intent
import android.graphics.Paint
import android.net.http.SslError
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.dialog.IADialog
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.my_cart.MyCartActivity
import com.app.ia.ui.product_detail.adapter.ProductImageAdapter
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.*
import com.kenilt.loopingviewpager.scroller.AutoScroller
import kotlinx.android.synthetic.main.activity_pdf_preview.view.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.content_container
import kotlinx.android.synthetic.main.activity_product_detail.toolbar
import kotlinx.android.synthetic.main.common_header.view.*
import java.lang.StringBuilder

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
            productDetailWebView.webViewClient = webViewClient
            productDetailWebView.settings.javaScriptEnabled = true

            val head1 = "<head><style>@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/linotte_regular.otf');}body {font-family: 'verdana';}</style></head>"
            val text = "<html>$head1<body style=font-family:arial>${it.getDescription()}</body></html>"
            productDetailWebView.loadDataWithBaseURL("", text, "text/html", "utf-8", "")
            //productDetailWebView.loadData(it.getDescription(), "text/html; charset=utf-8", "UTF-8")

            val bannerPagerAdapter = ProductImageAdapter(this@ProductDetailActivity, it?.images)
            viewPagerBanner.adapter = bannerPagerAdapter
            viewPagerIndicator.setViewPager(viewPagerBanner)
            val autoScroller = AutoScroller(viewPagerBanner, lifecycle, 3000)
            autoScroller.isAutoScroll = false
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

    private var webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            //injectCSS()
        }
    }

    private fun injectCSS() {
        try {
            val inputStream = assets.open("style.css")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            productDetailWebView.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}