package com.app.ia.ui.setting

import android.app.Activity
import android.content.Intent
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySettingBinding

class SettingViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySettingBinding

    fun setVariable(mBinding: ActivitySettingBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.setting))
    }
}