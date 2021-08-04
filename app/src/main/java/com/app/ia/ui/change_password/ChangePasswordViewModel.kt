package com.app.ia.ui.change_password

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityChangePasswordBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class ChangePasswordViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityChangePasswordBinding

    fun setVariable(mBinding: ActivityChangePasswordBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.change_password_wo_))
    }

    fun onChangePasswordClick() {
        val oldPassword = mBinding.edtTextOldPassword.text.toString()
        val newPassword = mBinding.edtTextNewPassword.text.toString()
        val confirmPassword = mBinding.edtTextConfirmPassword.text.toString()

        when {
            oldPassword.isEmpty() -> {
                IADialog(mActivity, mActivity.getString(R.string.please_enter_old_password), true)
            }
            oldPassword.length < 6 -> {
                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
            }
            newPassword.isEmpty() -> {
                IADialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
            }
            newPassword.length < 6 -> {
                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
            }
            confirmPassword.isEmpty() -> {
                IADialog(mActivity, mActivity.getString(R.string.please_enter_confirm_password), true)
            }
            confirmPassword.length < 6 -> {
                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
            }
            confirmPassword != newPassword -> {
                IADialog(mActivity, mActivity.getString(R.string.confirm_password_should_be_same_as_new_password), true)
            }
            else -> {
                val requestParams = HashMap<String, String>()
                requestParams["old_password"] = oldPassword
                requestParams["new_password"] = newPassword
                changePasswordObserver(requestParams)
            }
        }
    }

    private fun changePassword(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.changePassword(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun changePasswordObserver(requestParams: HashMap<String, String>) {
        changePassword(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            mActivity.finish()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

}