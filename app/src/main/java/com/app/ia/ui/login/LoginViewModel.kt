package com.app.ia.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.provider.Settings
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Patterns
import android.view.View
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
import com.app.ia.ui.otp.OTPActivity
import com.app.ia.ui.signup.SignUpActivity
import com.app.ia.utils.*
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

class LoginViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var androidId: String
    private var messageId = ""

    @SuppressLint("HardwareIds")
    fun setVariable(mBinding: ActivityLoginBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.login))
        mBinding.header.imageViewBack.visibility = View.INVISIBLE
        requestHint()
        doNotHaveAccountText()
        storeDeviceToken()
        androidId = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID)
        val appSignatureHashHelper = AppSignatureHashHelper(mActivity)
        messageId = appSignatureHashHelper.appSignatures[0]
        AppLogger.d(messageId)
    }

    private fun storeDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                AppLogger.w("Fetching FCM registration token failed" + task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val fcmToken = task.result
            AppLogger.d("device token : $fcmToken")
            AppPreferencesHelper.getInstance().deviceToken = fcmToken
        })
    }

    fun validateFields(): Boolean{
        var isValid = true

        mBinding.tilTextMobileNumber.error = null
        mBinding.tilTextPassword.error = null

        mBinding.tilTextMobileNumber.isErrorEnabled = false
        mBinding.tilTextPassword.isErrorEnabled = false

        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        val password = mBinding.edtTextPassword.text.toString()

        if (TextUtils.isEmpty(mobileNumber)) {
            mBinding.tilTextMobileNumber.error = mActivity.getString(R.string.enter_email_mobile_no)
            isValid = false
        }

        if (password.isEmpty()) {
            mBinding.tilTextPassword.error = mActivity.getString(R.string.enter_your_password)
            isValid = false
        }

        return isValid
    }

    fun onLoginClick() {

        (baseRepository.callback).hideKeyboard()
        val mobileNumber = mBinding.edtTextMobileNumber.text.toString()
        val password = mBinding.edtTextPassword.text.toString()
        val flag: Boolean = true

        if(!validateFields()){
            return
        }

        if (!TextUtils.isEmpty(password) && flag) {
            val requestParams = HashMap<String, String>()
            requestParams["country_code"] = "+91"
            requestParams["phone"] = mobileNumber
            requestParams["password"] = password
            requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
            requestParams["device_type"] = "1"
            requestParams["device_id"] = androidId
            requestParams["login_through"] = "password"
            requestParams["message_id"] = messageId
            requestParams["latitude"] = AppPreferencesHelper.getInstance().mCurrentLat.toString()
            requestParams["longitude"] = AppPreferencesHelper.getInstance().mCurrentLng.toString()
            setupObservers(requestParams)
        }
    }

    fun onForgotPasswordClick() {
        mActivity.startActivity<ForgotPasswordActivity>()
    }

    fun skipForNow() {
        mActivity.startActivity<HomeActivity> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        val options = CredentialsOptions.Builder().forceEnableSaveDialog().build()
        val pendingIntent = Credentials.getClient(getActivityNavigator()!!, options).getHintPickerIntent(hintRequest)
        getActivityNavigator()!!.startIntentSenderForResult(pendingIntent.intentSender, AppRequestCode.PHONE_REQUEST, null, 0, 0, 0)
    }

    /*private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }*/

    private fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
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

                            if (users.data?.isUserVerified == 0) {
                                val dialog = IADialog(mActivity, "", users.message, true)
                                dialog.setOnItemClickListener(object: IADialog.OnClickListener{
                                    override fun onPositiveClick() {
                                        mActivity.startActivity<OTPActivity> {
                                            putExtra("countryCode", users.data?.countryCode)
                                            putExtra("mobileNumber", users.data?.phone)
                                            putExtra("otp", users.data?.otpNumber)
                                        }
                                    }
                                    override fun onNegativeClick() {

                                    }
                                })

                            } else {
                                AppPreferencesHelper.getInstance().userData = users.data!!
                                mActivity.startActivityWithFinish<HomeActivity> {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            }

                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        IADialog(mActivity, it.message!!, true)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

}