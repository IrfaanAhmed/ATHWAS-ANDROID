package com.app.ia.ui.change_password

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityChangePasswordBinding
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding, ChangePasswordViewModel>() {

    private var mActivityChangePasswordBinding: ActivityChangePasswordBinding? = null
    private var mChangePasswordViewModel: ChangePasswordViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun getViewModel(): ChangePasswordViewModel {
        return mChangePasswordViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityChangePasswordBinding = getViewDataBinding()
        mActivityChangePasswordBinding?.lifecycleOwner = this
        mChangePasswordViewModel?.setActivityNavigator(this)
        mChangePasswordViewModel?.setVariable(mActivityChangePasswordBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ChangePasswordViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mChangePasswordViewModel = ViewModelProvider(this, factory).get(ChangePasswordViewModel::class.java)
    }
}