package com.app.ia.ui.otp_verify

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityOtpVerifyBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.spanly.Spanly
import com.app.ia.spanly.clickable
import com.app.ia.spanly.color
import com.app.ia.spanly.font
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import com.app.ia.utils.getColorCompat
import com.app.ia.utils.toast
import com.app.ia.receiver.SMSReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import java.util.*
import java.util.concurrent.TimeUnit

class OTPVerifyViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), SMSReceiver.OTPReceiveListener {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOtpVerifyBinding

    private var myCountDownTimer: MyCountDownTimer? = null
    private var resendTimeInSec = 60
    var isTimeFinished = MutableLiveData(false)
    var expireTime = MutableLiveData<String>()

    var countryCode = MutableLiveData("")
    var mobileNumber = MutableLiveData("")
    var otp = MutableLiveData("")
    var otpFor = MutableLiveData("")

    private var smsReceiver: SMSReceiver? = null

    fun setVariable(mBinding: ActivityOtpVerifyBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.otp))
        startTimer()
        didNotReceiveOtpText()
    }

    fun setIntent(intent: Intent) {
        countryCode.value = intent.getStringExtra("countryCode")
        mobileNumber.value = intent.getStringExtra("mobileNumber")
        otp.value = intent.getStringExtra("otp")
        otpFor.value = intent.getStringExtra("otpFor")
    }

    fun onVerifyClick() {
        if (mBinding.pinView.text!!.isNotEmpty() && mBinding.pinView.text!!.toString() == otp.value!!) {

            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = countryCode.value!!
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = mBinding.pinView.text.toString()
            requestParams["otp_for"] = otpFor.value!!
            requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
            requestParams["device_type"] = "1"
            setupObservers(requestParams, true)
        } else {
            IADialog(mActivity, mActivity.getString(R.string.please_enter_otp), true)
        }
    }

    private fun startTimer() {

        if (myCountDownTimer != null) {
            myCountDownTimer?.cancel()
        }

        isTimeFinished.value = false

        myCountDownTimer = MyCountDownTimer((resendTimeInSec * 1000).toLong(), 1000)
        myCountDownTimer?.start()
    }

    inner class MyCountDownTimer internal constructor(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            val progress = (millisUntilFinished / 1000).toInt()
            val time = String.format(Locale("en"), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            expireTime.value = time
            if (progress == 0) {
                isTimeFinished.value = true
            }
        }

        override fun onFinish() {
            isTimeFinished.value = true
        }
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver?.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            mActivity.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(mActivity)
            val task: Task<Void> = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun didNotReceiveOtpText() {

        val fontSemiBold: Typeface = ResourcesCompat.getFont(mActivity, R.font.linotte_bold)!!
        val spanly = Spanly()

        spanly.append(mActivity.getString(R.string.didn_t_receive_the_otp)).space().append(mActivity.getString(R.string.resend_otp), color(mActivity.getColorCompat(R.color.colorPrimary)), font(fontSemiBold), clickable(View.OnClickListener {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = countryCode.value!!
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_for"] = "register"
            setupObservers(requestParams, false)
        }))

        mBinding.txtViewResendOTP.text = spanly
        mBinding.txtViewResendOTP.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onCleared() {
        super.onCleared()
        if (myCountDownTimer != null) {
            myCountDownTimer?.cancel()
        }

        if (smsReceiver != null) {
            smsReceiver?.let { mActivity.unregisterReceiver(it) }
        }
    }

    override fun onOTPReceived(otp: String?) {
        mBinding.pinView.setText(otp)
    }

    override fun onOTPTimeOut() {

    }

    override fun onOTPReceivedError(error: String?) {

    }

    private fun apiCalling(requestParams: HashMap<String, String>, verifyOTP: Boolean) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            if (verifyOTP) {
                emit(Resource.success(data = baseRepository.verifyOTP(requestParams)))
            } else {
                emit(Resource.success(data = baseRepository.resendOTP(requestParams)))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>, verifyOTP: Boolean) {

        apiCalling(requestParams, verifyOTP).observe(mBinding.lifecycleOwner!!, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {

                        resource.data?.let { users ->

                            mActivity.toast(users.message)
                            val localBroadCast = LocalBroadcastManager.getInstance(mActivity)
                            val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
                            localBroadCast.sendBroadcast(intent)
                            if (verifyOTP) {
                                mActivity.finish()
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (it.message!!.isNotEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}