package com.app.ia.ui.order_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityPdfPreviewBinding
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_pdf_preview.*
import kotlinx.android.synthetic.main.common_header.view.*


class PdfPreviewActivity : BaseActivity<ActivityPdfPreviewBinding, PdfPreviewViewModel>() {

    private var mBinding: ActivityPdfPreviewBinding? = null
    private var mViewModel: PdfPreviewViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pdf_preview
    }

    override fun getViewModel(): PdfPreviewViewModel {
        return mViewModel!!
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        setOnApplyWindowInset(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        val fileName = intent.getStringExtra("fileName")
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.builtInZoomControls = true;
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=$fileName")



    }

    private fun setViewModel() {
        val factory = ViewModelFactory(PdfPreviewViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(PdfPreviewViewModel::class.java)
    }
}