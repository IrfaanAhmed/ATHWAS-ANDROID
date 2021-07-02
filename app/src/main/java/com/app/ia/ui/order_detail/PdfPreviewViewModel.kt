package com.app.ia.ui.order_detail

import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityPdfPreviewBinding
import kotlinx.android.synthetic.main.activity_pdf_preview.*

class PdfPreviewViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityPdfPreviewBinding

    fun setVariable(mBinding: ActivityPdfPreviewBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("Invoice Preview")

        baseRepository.callback.showProgress()
        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // do your stuff here
                baseRepository.callback.hideProgress()
            }
        }
    }
}