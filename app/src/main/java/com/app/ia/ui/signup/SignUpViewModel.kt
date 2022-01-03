package com.app.ia.ui.signup

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.provider.Settings
import android.text.method.LinkMovementMethod
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySignUpBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.ContentType
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.spanly.Spanly
import com.app.ia.spanly.clickable
import com.app.ia.spanly.color
import com.app.ia.spanly.font
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.otp.OTPActivity
import com.app.ia.ui.webview.WebViewActivity
import com.app.ia.utils.*
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.Dispatchers

class SignUpViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySignUpBinding
    private lateinit var androidId: String
    private var messageId = ""

    @SuppressLint("HardwareIds")
    fun setVariable(mBinding: ActivitySignUpBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.sign_up))
        alreadyHaveAccountText()
        androidId = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID)
        val appSignatureHashHelper = AppSignatureHashHelper(mActivity)
        messageId = appSignatureHashHelper.appSignatures[0]
        AppLogger.d(messageId)
    }

    fun skipForNow() {
        mActivity.startActivity<HomeActivity>()
    }

    private fun userRegister(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.userRegister(requestParams)))
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
                            mActivity.startActivity<OTPActivity> {
                                putExtra("countryCode", "+91")
                                putExtra("mobileNumber", mBinding.editTextMobile.text.toString())
                                putExtra("otp", users.data!!.otpNumber)
                            }
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

    fun onUserSignUp() {
        mBinding.editTextName.setText(mBinding.editTextName.text.toString().trim())
        mBinding.edtTextEmail.setText(mBinding.edtTextEmail.text.toString().trim())

        val name = mBinding.editTextName.text.toString()
        val email = mBinding.edtTextEmail.text.toString()
        val phone = mBinding.editTextMobile.text.toString().trim()
        val password = mBinding.edtTextPassword.text.toString()
        var haveName = false
        var havePass = false
        var haveEmail = false
        var haveMobile = false
        mBinding.tilTextName.error = null
        mBinding.tilTextEmail.error = null
        mBinding.tilTextMobile.error = null
        mBinding.tilTextPassword.error = null

        mBinding.tilTextName.isErrorEnabled = false
        mBinding.tilTextEmail.isErrorEnabled = false
        mBinding.tilTextMobile.isErrorEnabled = false
        mBinding.tilTextPassword.isErrorEnabled = false

        if (name.length < 3 || name.length > 30) {
            //IADialog(mActivity, mActivity.getString(R.string.msg_name_validation_length), true)
            mBinding.tilTextName.error = mActivity.getString(R.string.msg_name_validation_length)
        } else if (ValidationUtils.isHaveLettersOnly(name)) {
            //IADialog(mActivity, mActivity.getString(R.string.msg_name_validation), true)
            mBinding.tilTextName.error = mActivity.getString(R.string.msg_name_validation)
        } else {
            haveName = true
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            //IADialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
            mBinding.tilTextMobile.error = mActivity.getString(R.string.enter_valid_mobile_no)
        } else {
            haveMobile = true
        }
        if (email.length < 9 || email.length > 320) {
            //IADialog(mActivity, mActivity.getString(R.string.msg_email_validation_length), true)
            mBinding.tilTextEmail.error = mActivity.getString(R.string.msg_email_validation_length)
        } else if (!ValidationUtils.isValidEmail(email)) {
            //IADialog(mActivity, mActivity.getString(R.string.enter_valid_email), true)
            mBinding.tilTextEmail.error = mActivity.getString(R.string.enter_valid_email)
        } else {
            haveEmail = true
        }
        if (password.contains(" ")) {
            //IADialog(mActivity, mActivity.getString(R.string.invalid_password_format), true)
            mBinding.tilTextPassword.error = mActivity.getString(R.string.invalid_password_format)
        } else if (password.length < 6 || password.length > 20) {
            //IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
            mBinding.tilTextPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
        } else {
            havePass = true
        }

        if (haveName &&
            havePass &&
            haveEmail &&
            haveMobile
        ) {
            if (!mBinding.checkBox.isChecked) {
                IADialog(mActivity, mActivity.getString(R.string.accept_terms_n_condition), true)

            } else {
                val requestParams = HashMap<String, String>()
                requestParams["username"] = name
                requestParams["country_code"] = "+91"
                requestParams["phone"] = phone
                requestParams["email"] = email
                requestParams["password"] = password
                requestParams["message_id"] = messageId
                requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
                requestParams["device_type"] = "1"
                requestParams["device_id"] = androidId
                setupObservers(requestParams)
                //mActivity.toast("SuccessFully")
            }


        }
    }

    private fun validateNumber(phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(91)
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }

        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    fun onTermOfUseClick() {
        mActivity.startActivity<WebViewActivity> {
            putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_of_use))
            putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_OF_USE.contentType)
        }
        /*mActivity.startActivity<WebViewActivity> {
            putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_of_use))
            putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_OF_USE.contentType)
        }*/
    }

    private fun alreadyHaveAccountText() {
        baseRepository.callback.hideKeyboard()
        val fontSemiBold: Typeface = ResourcesCompat.getFont(mActivity, R.font.linotte_semi_bold)!!
        val spanly = Spanly()
        spanly.append(mActivity.getString(R.string.already_have_an_account)).space().append(
            mActivity.getString(R.string.sign_in),
            color(mActivity.getColorCompat(R.color.dark_green)),
            font(fontSemiBold),
            clickable({
                mActivity.finish()
            })
        )

        mBinding.txtViewAlreadyLogin.text = spanly
        mBinding.txtViewAlreadyLogin.movementMethod = LinkMovementMethod.getInstance()
    }
}