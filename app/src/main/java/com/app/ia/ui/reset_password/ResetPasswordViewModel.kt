package com.app.ia.ui.reset_password

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityResetPasswordBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.receiver.SMSReceiver
import com.app.ia.ui.login.LoginActivity
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivityWithFinish
import com.app.ia.utils.toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import java.util.*

class ResetPasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), SMSReceiver.OTPReceiveListener {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityResetPasswordBinding

    var countryCode = MutableLiveData("")
    var mobileNumber = MutableLiveData("123")
    var otp = MutableLiveData("")

    private var smsReceiver: SMSReceiver? = null

    fun setVariable(mBinding: ActivityResetPasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.reset_password))
        startSMSListener()
    }

    fun setIntent(intent: Intent) {
        countryCode.value = intent.getStringExtra("countryCode")
        mobileNumber.value = intent.getStringExtra("mobileNumber")
        otp.value = intent.getStringExtra("otp")

        IADialog(mActivity, "OTP is : " + otp.value, true)
    }

    fun onResetClick() {

        val otpPin = mBinding.pinView.text.toString()
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()
        if (otpPin.isEmpty() || otpPin.length < 4) {
            IADialog(mActivity, "Please enter 4 digits OTP", true)
        } else if (newPassword.isEmpty()) {
            IADialog(mActivity, "Please enter new password", true)
        } else if (confirmPassword.isEmpty()) {
            IADialog(mActivity, "Please enter confirm password", true)
        } else if (checkPasswordLength(newPassword, confirmPassword)) {
            IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
        } else if (newPassword != confirmPassword) {
            IADialog(mActivity, "Password doesn't match", true)
        } else {
            val requestParams = java.util.HashMap<String, String>()
            requestParams["country_code"] = "+91"
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = otpPin
            requestParams["new_password"] = newPassword
            setupObservers(requestParams)
        }
    }

    private fun checkPasswordLength(newPassword: String, confirmPassword: String): Boolean {
        return (newPassword.length < 6 || confirmPassword.length < 6)
    }


    private fun userRegister(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.updateForgotPassword(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        userRegister(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            mActivity.startActivityWithFinish<LoginActivity> {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
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

    override fun onCleared() {
        super.onCleared()
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

}