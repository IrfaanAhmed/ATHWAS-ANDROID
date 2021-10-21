package com.app.ia.ui.payment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.apiclient.Api
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityPaymentBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.utils.*
import kotlinx.coroutines.Dispatchers
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class PaymentViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityPaymentBinding

    var rsaKeyUrl = MutableLiveData("")
    var accessCode = MutableLiveData("")
    var orderId = MutableLiveData("")
    var amount = MutableLiveData("")
    var currency = MutableLiveData("")
    var paymentFor = MutableLiveData("")

    var encVal = MutableLiveData("")
    var vResponse = MutableLiveData("")

    fun setVariable(mBinding: ActivityPaymentBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        rsaKeyUrl.value = intent.getStringExtra(AvenuesParams.RSA_KEY_URL)
        accessCode.value = intent.getStringExtra(AvenuesParams.ACCESS_CODE)!!
        orderId.value = intent.getStringExtra(AvenuesParams.ORDER_ID)!!
        amount.value = intent.getStringExtra(AvenuesParams.AMOUNT)
        currency.value = intent.getStringExtra(AvenuesParams.CURRENCY)
        paymentFor.value = intent.getStringExtra(AvenuesParams.PAYMENT_FOR)
        title.set("")

        val params = HashMap<String, String>()
        params[AvenuesParams.ACCESS_CODE] = accessCode.value!!
        params[AvenuesParams.ORDER_ID] = orderId.value!!
        getRSAKeyObservers(params)

    }


    private fun getRSAKey(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = RetrofitFactory.getPaymentInstance().getRSAKey(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getRSAKeyObservers(requestParams: HashMap<String, String>) {

        getRSAKey(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            vResponse.value = users.body()!!.string()
                            val vEncVal = StringBuffer()
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, amount.value!!))
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, currency.value!!))
                            encVal.value = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length - 1), vResponse.value!!)
                            callPaymentURl()
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }
                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    fun callPaymentURl() {
        class MyJavaScriptInterface {
            @JavascriptInterface
            fun processHTML(html: String) {
                // process the html source code to get final status of transaction
                var status: String? = null
                status = if (html.indexOf("Failure") != -1) {
                    "Transaction Declined!"
                } else if (html.indexOf("Success") != -1) {
                    "Transaction Successful!"
                } else if (html.indexOf("Aborted") != -1) {
                    "Transaction Cancelled!"
                } else {
                    "Status Not Known!"
                }
                /* val intent = Intent(getApplicationContext(), StatusActivity::class.java)
                 intent.putExtra("transStatus", status)
                 startActivity(intent)*/
            }
        }

        mBinding.paymentWebView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")

        mBinding.paymentWebView.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                Log.d("Payment Url", ""+url?.toString())
                if (url.toString().contains("upi://pay?pa")) {
                    val intent = Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url.toString()));
                    mActivity.startActivity(intent)
                    return null
                }
                return super.shouldInterceptRequest(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                Log.d("Payment Url", ""+request?.url?.toString())
                if (request?.url.toString().contains("upi://pay?pa")) {
                    val intent = Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(request?.url.toString()));
                    mActivity.startActivity(intent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                baseRepository.callback.hideProgress()
                if (url?.indexOf("/ccavResponseHandler.jsp") != -1) {
                    mBinding.paymentWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }

                if (url?.indexOf("/complete_payment") != -1) {
                    val intent = Intent()
                    intent.putExtra("data", "")
                    mActivity.setResult(Activity.RESULT_OK, intent)
                    mActivity.finish()
                    mBinding.paymentWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                baseRepository.callback.showProgress()
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {

                var message = mActivity.getString(R.string.ssl_error_title)
                when (error!!.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = mActivity.getString(R.string.ssl_untrusted)
                    SslError.SSL_EXPIRED -> message = mActivity.getString(R.string.ssl_expired)
                    SslError.SSL_IDMISMATCH -> message = mActivity.getString(R.string.ssl_id_mismatch)
                    SslError.SSL_NOTYETVALID -> message = mActivity.getString(R.string.ssl_not_yet_valid)
                }
                message += mActivity.getString(R.string.ssl_do_you_want_continue)

                val dialog = IADialog(mActivity, mActivity.getString(R.string.ssl_error_title), message, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel), false)
                dialog.setOnItemClickListener(object : IADialog.OnClickListener {
                    override fun onPositiveClick() {
                        handler!!.proceed()
                    }

                    override fun onNegativeClick() {
                        handler!!.cancel()
                    }
                })
            }
        }


        try {
            val postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(accessCode.value!!, "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode("376194", "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(orderId.value!!, "UTF-8") + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(Api.REDIRECT_URL,
                "UTF-8") + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(Api.CANCEL_URL, "UTF-8") + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal.value!!, "UTF-8") + "&" + AvenuesParams.MERCHANT_PARAM_1 + "=" + URLEncoder.encode(paymentFor.value!!, "UTF-8") + "&" + AvenuesParams.MERCHANT_PARAM_2 + "=" + URLEncoder.encode(AppPreferencesHelper.getInstance().userID, "UTF-8")
            mBinding.paymentWebView.postUrl(AppConstants.TRANS_URL, postData.toByteArray())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}