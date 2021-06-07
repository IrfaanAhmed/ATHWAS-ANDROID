package com.app.ia.ui.setting

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivitySettingBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.utils.gone
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.common_header.view.*

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {

    private var mBinding: ActivitySettingBinding? = null
    private var mViewModel: SettingViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun getViewModel(): SettingViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.gone()
        toolbar.ivSearchIcon.gone()
        toolbar.ivEditProfileIcon.gone()

        switchNotification.isChecked = AppPreferencesHelper.getInstance().allowNotification == 1

        switchNotification.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppPreferencesHelper.getInstance().allowNotification = 1
            } else {
                AppPreferencesHelper.getInstance().allowNotification = 0
            }
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SettingViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SettingViewModel::class.java)
    }
}