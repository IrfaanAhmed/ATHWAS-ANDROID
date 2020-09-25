package com.app.ia.ui.otp

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.base.BaseActivity
import com.app.ia.databinding.ActivityOtpBinding
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseRepository
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_otp.*

class OTPActivity : BaseActivity<ActivityOtpBinding, OTPViewModel>() {

    private var mBinding: ActivityOtpBinding? = null
    private var mViewModel: OTPViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_otp
    }

    override fun getViewModel(): OTPViewModel {
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
        val factory = ViewModelFactory(OTPViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OTPViewModel::class.java)
    }
}