package com.app.ia.ui.reset_password

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.R
import com.app.ia.base.BaseActivity
import com.app.ia.databinding.ActivityResetPasswordBinding
import com.app.ia.BR
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseRepository
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : BaseActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>() {

    private var mBinding: ActivityResetPasswordBinding? = null
    private var mViewModel: ResetPasswordViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_reset_password
    }

    override fun getViewModel(): ResetPasswordViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ResetPasswordViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ResetPasswordViewModel::class.java)
    }
}