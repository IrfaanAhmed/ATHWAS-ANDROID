package com.app.ia.ui.edit_profile

import android.app.Activity
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityEditProfileBinding
import com.app.ia.ui.change_password.ChangePasswordActivity
import com.app.ia.utils.startActivity

class EditProfileViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityEditProfileBinding

    fun setVariable(mBinding: ActivityEditProfileBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.edit_profile))
    }

    fun onChangePasswordClick() {
        mActivity.startActivity<ChangePasswordActivity>()
    }

}