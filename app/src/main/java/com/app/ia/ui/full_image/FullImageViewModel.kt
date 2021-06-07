package com.app.ia.ui.full_image

import android.app.Activity
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityFullImageBinding

class FullImageViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityFullImageBinding

    fun setVariable(mBinding: ActivityFullImageBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
    }
}