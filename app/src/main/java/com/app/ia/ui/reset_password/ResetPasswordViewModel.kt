package com.app.ia.ui.reset_password

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityResetPasswordBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.receiver.SMSReceiver
import com.app.ia.ui.home.HomeActivity
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
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()
        mBinding.tilTextConfirmPassword.error = null
        mBinding.tilTextConfirmPassword.isErrorEnabled = false
        mBinding.tilTextNewPassword.error = null
        mBinding.tilTextNewPassword.isErrorEnabled = false
        mBinding.pinViewError.visibility = View.GONE

        var hasOtp = false
        var hasNewPassword = false
        var hasConfirmPassword = false
        var hasMatchPassword = false

        if (mBinding.pinView.text!!.isEmpty()) {
            mBinding.pinViewError.text = mActivity.getString(R.string.please_enter_otp)
            mBinding.pinViewError.visibility = View.VISIBLE
        } else if (mBinding.pinView.text!!.toString() != otp.value!!) {
            mBinding.pinViewError.text = mActivity.getString(R.string.please_enter_valid_otp)
            mBinding.pinViewError.visibility = View.VISIBLE
        } else {
            hasOtp = true
        }
        if (newPassword.isEmpty()) {
            mBinding.tilTextConfirmPassword.error =
                mActivity.getString(R.string.please_enter_new_password)
        } else if (newPassword.length < 6 || newPassword.length > 15) {
            mBinding.tilTextConfirmPassword.error =
                mActivity.getString(R.string.new_password_validation_msg)
        } else {
            hasNewPassword = true
        }


        if (confirmPassword.isEmpty()) {
            mBinding.edtTextConfirmPassword.error =
                mActivity.getString(R.string.enter_conform_password)
        } else if (confirmPassword.length < 6 || confirmPassword.length > 15) {
            mBinding.edtTextConfirmPassword.error =
                mActivity.getString(R.string.confirm_password_validation_msg)
        } else {
            hasConfirmPassword = true
        }


        if (confirmPassword != newPassword) {
            mBinding.edtTextConfirmPassword.error =
                mActivity.getString(R.string.confirm_new_matched_validation_msg)
        } else {
            hasMatchPassword = true
        }
        if (hasOtp && hasNewPassword && hasConfirmPassword && hasMatchPassword) {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = "+${countryCode.value!!}"
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = mBinding.pinView.text.toString()
            requestParams["new_password"] = newPassword
            setupObservers(requestParams)
        }
    }


    /* fun onResetClick() {

        val otpPin = mBinding.pinView.text.toString()
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()

        mBinding.tilTextNewPassword.error = null
        mBinding.tilTextConfirmPassword.error = null

        mBinding.tilTextNewPassword.isErrorEnabled = false
        mBinding.tilTextConfirmPassword.isErrorEnabled = false

        mBinding.pinViewError.visibility= View.GONE

        if (otpPin.isEmpty() || otpPin.length < 4) {
//            IADialog(mActivity, "Please enter 4 digits OTP", true)
            mBinding.pinViewError.visibility= View.VISIBLE
        } else if (newPassword.isEmpty()) {
            //IADialog(mActivity, "Please enter new password", true)
            mBinding.tilTextNewPassword.error =  "Please enter new password"
        } else if (confirmPassword.isEmpty()) {
            //IADialog(mActivity, "Please enter confirm password", true)
            mBinding.tilTextConfirmPassword.error =  "Please enter confirm password"
        } else if (newPassword.length < 6) {
            mBinding.edtTextNewPassword.error=mActivity.getString(R.string.password_should_be_min_6_char)
//            IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
        } else if (confirmPassword.length < 6) {
            mBinding.edtTextConfirmPassword.error=mActivity.getString(R.string.password_should_be_min_6_char)
//            IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
        } else if (newPassword != confirmPassword) {
//            IADialog(mActivity, "Password doesn't match", true)
            mBinding.edtTextConfirmPassword.error="Password doesn't match"
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = "+91"
            requestParams["phone"] = mobileNumber.value!!
            requestParams["otp_number"] = otpPin
            requestParams["new_password"] = newPassword
            setupObservers(requestParams)
        }
    }*/

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
                            //mActivity.toast(users.message)
                            val dialog = IADialog(mActivity, "", users.message, true)
                            dialog.setOnItemClickListener(object: IADialog.OnClickListener{
                                override fun onPositiveClick() {
                                    mActivity.startActivityWithFinish<LoginActivity> {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                }
                                override fun onNegativeClick() {

                                }
                            })

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