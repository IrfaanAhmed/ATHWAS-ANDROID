package com.app.ia.ui.forgot_password

import android.app.Activity
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityForgotPasswordBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.reset_password.ResetPasswordActivity
import com.app.ia.utils.CommonUtils.isEmailValid
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivity
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.Dispatchers
import java.util.*
import java.util.regex.Pattern

class ForgotPasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityForgotPasswordBinding

    fun setVariable(mBinding: ActivityForgotPasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.forgot_password_title))
    }

    fun onSendClick() {

        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        val flag: Boolean

        if (TextUtils.isEmpty(mobileNumber)) {
            IADialog(mActivity, mActivity.getString(R.string.enter_email_mobile_no), true)
            return
        } else {
            flag = if (Pattern.matches("[0-9]+", mobileNumber)) {
                if (mobileNumber.length < 7 || mobileNumber.length > 15) {
                    IADialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
                    return
                } else {
                    true
                }
            } else {
                if (!isEmailValid(mobileNumber)) {
                    IADialog(mActivity, mActivity.getString(R.string.enter_valid_email), true)
                    return
                } else {
                    true
                }
            }
        }

        if (flag) {
            AppPreferencesHelper.getInstance().authToken = ""
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = "+91"
            requestParams["phone"] = mobileNumber
            requestParams["otp_for"] = "forgot_password"
            setupObservers(requestParams)
        }
    }

    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }

    private fun validateNumber(countryCode: String, phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode))
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }


    private fun apiCalling(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.resendOTP(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {

        apiCalling(requestParams).observe(mBinding.lifecycleOwner!!, {
            it.let { resource ->
                when (resource.status) {

                    Status.SUCCESS -> {

                        resource.data?.let { users ->
                            mActivity.startActivity<ResetPasswordActivity> {
                                putExtra("countryCode", users.data?.countryCode)
                                putExtra("mobileNumber", users.data?.phone)
                                putExtra("otp", users.data?.otpNumber)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}