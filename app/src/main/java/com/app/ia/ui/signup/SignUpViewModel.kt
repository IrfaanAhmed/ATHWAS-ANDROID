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
import kotlinx.coroutines.Dispatchers

class SignUpViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySignUpBinding
    private lateinit var androidId: String

    @SuppressLint("HardwareIds")
    fun setVariable(mBinding: ActivitySignUpBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.sign_up))
        alreadyHaveAccountText()
        androidId = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID)
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
                            if (users.status == "success") {
                                mActivity.toast(users.message)
                                mActivity.startActivity<OTPActivity> {
                                    putExtra("countryCode", "+91")
                                    putExtra("mobileNumber", mBinding.editTextMobile.text.toString())
                                    putExtra("otp", users.data!!.otpNumber)
                                }

                            } else {
                                IADialog(mActivity, users.message, true)
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

        val name = mBinding.editTextName.text.toString().trim()
        val email = mBinding.edtTextEmail.text.toString().trim()
        val phone = mBinding.editTextMobile.text.toString().trim()
        val password = mBinding.edtTextPassword.text.toString().trim()

        if (phone.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_mobile_no), true)
        } else if (phone.length < 6) {
            IADialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
        } else if (name.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_name), true)
        } else if (name.length < 2) {
            IADialog(mActivity, mActivity.getString(R.string.name_should_be_min_2_char), true)
        } else if (email.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_email), true)
        } else if (!email.isValidEmail()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_valid_email), true)
        } else if (password.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_password), true)
        } else if (password.length < 6) {
            IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
        } else {

            val requestParams = HashMap<String, String>()
            requestParams["username"] = name
            requestParams["country_code"] = "+91"
            requestParams["phone"] = phone
            requestParams["email"] = email
            requestParams["password"] = password
            requestParams["device_token"] = AppPreferencesHelper.getInstance().deviceToken
            requestParams["device_type"] = "1"
            requestParams["device_id"] = androidId
            setupObservers(requestParams)
        }
    }

    fun onTermOfUseClick() {
        mActivity.startActivity<WebViewActivity> {
            putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_of_use))
            putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_OF_USE.contentType)
        }
    }

    private fun alreadyHaveAccountText() {
        baseRepository.callback.hideKeyboard()
        val fontSemiBold: Typeface = ResourcesCompat.getFont(mActivity, R.font.linotte_semi_bold)!!
        val spanly = Spanly()
        spanly.append(mActivity.getString(R.string.already_have_an_account)).space().append(mActivity.getString(R.string.sign_in), color(mActivity.getColorCompat(R.color.dark_green)), font(fontSemiBold), clickable({
            mActivity.finish()
        }))

        mBinding.txtViewAlreadyLogin.text = spanly
        mBinding.txtViewAlreadyLogin.movementMethod = LinkMovementMethod.getInstance()
    }
}