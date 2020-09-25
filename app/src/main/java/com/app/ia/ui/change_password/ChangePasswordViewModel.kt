package com.app.ia.ui.change_password

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.Dispatchers

class ChangePasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityChangePasswordBinding

    fun setVariable(mBinding: ActivityChangePasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.change_password_wo_))
    }

}