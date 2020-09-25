package com.app.ia.ui.login

import android.app.Activity
import android.graphics.Typeface
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityLoginBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.spanly.Spanly
import com.app.ia.spanly.clickable
import com.app.ia.spanly.color
import com.app.ia.spanly.font
import com.app.ia.ui.forgot_password.ForgotPasswordActivity
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.signup.SignUpActivity
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.Resource
import com.app.ia.utils.getColorCompat
import com.app.ia.utils.startActivity
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityLoginBinding

    fun setVariable(mBinding: ActivityLoginBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.login))
        mBinding.header.imageViewBack.visibility = View.INVISIBLE
        requestHint()
        doNotHaveAccountText()
        storeDeviceToken()
    }

    private fun storeDeviceToken() {
        /* FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
             val deviceToken = instanceIdResult.token
             AppLogger.d("device token : $deviceToken")
             AppPreferencesHelper.getInstance().deviceToken = deviceToken
         }*/
    }

    fun onLoginClick() {

        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        //val ccp = mBinding.ccp
        val password = mBinding.edtTextPassword.text.toString()

        if (isValidPhoneNumber(mobileNumber)) {
            //if (validateNumber(ccp.selectedCountryCode, mobileNumber)) {
            when {
                mobileNumber.length < 6 -> {
                    IADialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
                }

                password.isEmpty() -> {
                    IADialog(mActivity, mActivity.getString(R.string.enter_your_password), true)
                }

                else -> {
                    val requestParams = HashMap<String, String>()
                    //requestParams["country_code"] = ccp.selectedCountryCodeWithPlus
                    requestParams["phone"] = mobileNumber
                    requestParams["password"] = password
                    requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
                    requestParams["device_type"] = "android"
                    //setupObservers(requestParams)
                }
            }
        } else {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_mobile_no), true)
        }
    }

    fun onForgotPasswordClick() {
        mActivity.startActivity<ForgotPasswordActivity>()
    }

    fun skipForNow() {
        mActivity.startActivity<HomeActivity>()
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()

        val options = CredentialsOptions.Builder().forceEnableSaveDialog().build()

        val pendingIntent = Credentials.getClient(getActivityNavigator()!!, options).getHintPickerIntent(hintRequest)
        getActivityNavigator()!!.startIntentSenderForResult(pendingIntent.intentSender, AppRequestCode.PHONE_REQUEST, null, 0, 0, 0)
    }

    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }

    @Suppress("unused")
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

    private fun doNotHaveAccountText() {

        val fontSemiBold: Typeface = ResourcesCompat.getFont(mActivity, R.font.linotte_semi_bold)!!
        val spanly = Spanly()

        spanly.append(mActivity.getString(R.string.don_t_have_an_account_yet)).space().append(mActivity.getString(R.string.sign_up), color(mActivity.getColorCompat(R.color.dark_green)), font(fontSemiBold), clickable({
            mActivity.startActivity<SignUpActivity>()
        }))

        mBinding.txtViewSignUp.text = spanly
        mBinding.txtViewSignUp.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getUsers(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.userLogin(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        getUsers(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {

                                /*if (users.data?.isUserVerified == 0) {
                                    mActivity.toast(users.message)
                                    mActivity.startActivity<OTPActivity> {
                                        putExtra("countryCode", users.data?.countryCode)
                                        putExtra("mobileNumber", users.data?.phone)
                                        putExtra("otp", users.data?.otpNumber)
                                    }
                                } else {
                                    AppPreferencesHelper.getInstance().userData = users.data!!
                                    mActivity.startActivityWithFinish<HomeActivity> {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                }*/
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

}