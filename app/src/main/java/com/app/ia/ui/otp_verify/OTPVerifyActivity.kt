package com.app.ia.ui.otp_verify

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityOtpVerifyBinding
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import com.app.ia.utils.visible
import kotlinx.android.synthetic.main.activity_otp_verify.*
import kotlinx.android.synthetic.main.common_header.view.*

class OTPVerifyActivity : BaseActivity<ActivityOtpVerifyBinding, OTPVerifyViewModel>() {

    private var mBinding: ActivityOtpVerifyBinding? = null
    private var mViewModel: OTPVerifyViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_otp_verify
    }

    override fun getViewModel(): OTPVerifyViewModel {
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

//        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OTPVerifyViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OTPVerifyViewModel::class.java)
    }
}