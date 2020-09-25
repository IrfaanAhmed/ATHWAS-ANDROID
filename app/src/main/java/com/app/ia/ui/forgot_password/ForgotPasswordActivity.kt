package com.app.ia.ui.forgot_password

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityForgotPasswordBinding
import com.app.ia.BR
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding, ForgotPasswordViewModel>() {

    private var mBinding: ActivityForgotPasswordBinding? = null
    private var mViewModel: ForgotPasswordViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_forgot_password
    }

    override fun getViewModel(): ForgotPasswordViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ForgotPasswordViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ForgotPasswordViewModel::class.java)
    }
}