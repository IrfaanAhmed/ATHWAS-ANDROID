package com.app.ia.ui.signup

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivitySignUpBinding
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import com.app.ia.BR
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity<ActivitySignUpBinding, SignUpViewModel>() {

    private var mActivitySignUpBinding: ActivitySignUpBinding? = null
    private var mSignUpViewModel: SignUpViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sign_up
    }

    override fun getViewModel(): SignUpViewModel {
        return mSignUpViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivitySignUpBinding = getViewDataBinding()
        mActivitySignUpBinding?.lifecycleOwner = this
        mSignUpViewModel?.setActivityNavigator(this)
        mSignUpViewModel?.setVariable(mActivitySignUpBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SignUpViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mSignUpViewModel = ViewModelProvider(this, factory).get(SignUpViewModel::class.java)
    }
}