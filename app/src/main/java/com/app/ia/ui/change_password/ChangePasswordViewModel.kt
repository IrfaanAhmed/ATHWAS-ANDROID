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

        mBinding.tilTextOldPassword.error = null
        mBinding.tilTextNewPassword.error = null
        mBinding.tilTextConfirmPassword.error = null

        mBinding.tilTextOldPassword.isErrorEnabled = false
        mBinding.tilTextNewPassword.isErrorEnabled = false
        mBinding.tilTextConfirmPassword.isErrorEnabled = false

       /* when {
            oldPassword.length < 6 || oldPassword.length > 20 -> {
                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
            }
            newPassword.contains(" ") -> {
//                IADialog(mActivity, mActivity.getString(R.string.invalid_password_format), true)
                mBinding.tilTextNewPassword.error=mActivity.getString(R.string.invalid_password_format)
            }
            newPassword.length < 6 || newPassword.length > 20 -> {
//                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
                mBinding.tilTextNewPassword.error=mActivity.getString(R.string.password_should_be_min_6_char)
            }
//            newPassword.isEmpty() -> {
//                IADialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
//            }
//            newPassword.length < 6 -> {
//                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
//            }
//            confirmPassword.isEmpty() -> {
//                IADialog(mActivity, mActivity.getString(R.string.please_enter_confirm_password), true)
//            }
//            confirmPassword.length < 6 -> {
//                IADialog(mActivity, mActivity.getString(R.string.password_should_be_min_6_char), true)
//            }
            confirmPassword != newPassword -> {
//                IADialog(mActivity, mActivity.getString(R.string.confirm_password_should_be_same_as_new_password), true)
                mBinding.tilTextConfirmPassword.error=mActivity.getString(R.string.confirm_password_should_be_same_as_new_password)
            }
            else -> {
                val requestParams = HashMap<String, String>()
                requestParams["old_password"] = oldPassword
                requestParams["new_password"] = newPassword
                changePasswordObserver(requestParams)
            }
        }*/
        var oldDone = false
        var newDone = false
        var confirmDone = false

        if (oldPassword.isEmpty()) {
            //  DriverDialog(mActivity, mActivity.getString(R.string.please_enter_old_password), true)
            mBinding.tilTextOldPassword.error =
                mActivity.getString(R.string.please_enter_old_password)
        } else if (oldPassword.length < 6 || oldPassword.length > 15) {
            mBinding.tilTextOldPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
            //DriverDialog(mActivity, mActivity.getString(R.string.old_password_validation_msg), true)
        }
        else{
            oldDone =true
        }
        if (newPassword.isEmpty()) {
            //  DriverDialog(mActivity, mActivity.getString(R.string.please_enter_new_password), true)
            mBinding.tilTextNewPassword.error =
                mActivity.getString(R.string.please_enter_new_password)
        } else if (newPassword.length < 6 || newPassword.length > 15) {
            mBinding.tilTextNewPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
        }else{
            newDone =true
        }
        if (confirmPassword.isEmpty()) {
            mBinding.tilTextConfirmPassword.error =
                mActivity.getString(R.string.please_enter_confirm_password)
        } else if (confirmPassword.length < 6 || confirmPassword.length > 15) {
            mBinding.tilTextConfirmPassword.error =
                mActivity.getString(R.string.password_should_be_min_6_char)
        } else if (confirmPassword != newPassword) {
            mBinding.tilTextConfirmPassword.error =
                mActivity.getString(R.string.confirm_password_should_be_same_as_new_password)
        }else{
            confirmDone =true
        }

        if (oldDone &&
            newDone &&
            confirmDone
        ) {
            val requestParams = HashMap<String, String>()
            requestParams["old_password"] = oldPassword
            requestParams["new_password"] = newPassword
            changePasswordObserver(requestParams)
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