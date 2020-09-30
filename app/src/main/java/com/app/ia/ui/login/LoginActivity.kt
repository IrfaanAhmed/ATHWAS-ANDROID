package com.app.ia.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityLoginBinding
import com.app.ia.utils.AppLogger
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import com.google.android.gms.auth.api.credentials.Credential
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    private var mActivityLoginBinding: ActivityLoginBinding? = null
    private var mSplashViewModel: LoginViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): LoginViewModel {
        return mSplashViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityLoginBinding = getViewDataBinding()
        mActivityLoginBinding?.lifecycleOwner = this
        mSplashViewModel?.setActivityNavigator(this)
        mSplashViewModel?.setVariable(mActivityLoginBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        KeyboardVisibilityEvent.setEventListener(this, this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    txtSkipForNow.visibility = View.GONE
                    txtViewSignUp.visibility = View.GONE
                } else {
                    txtSkipForNow.visibility = View.VISIBLE
                    txtViewSignUp.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(LoginViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mSplashViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppRequestCode.PHONE_REQUEST) {
            if (data != null) {
                val credential: Credential? = data.getParcelableExtra(Credential.EXTRA_KEY)
                val phoneNumber = credential?.id
                if (phoneNumber != null) {
                    edtTextMobileNumber.setText(formatPhoneNumber(phoneNumber))
                }
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return try {
            // phone must begin with '+'
            val phoneUtil = PhoneNumberUtil.getInstance()
            val numberProto = phoneUtil.parse(phoneNumber, "")
            val countryCode = numberProto.countryCode
            val nationalNumber = numberProto.nationalNumber
            AppLogger.i("code $countryCode")
            AppLogger.i("national number $nationalNumber")
            nationalNumber.toString()
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
            phoneNumber
        }
    }


}