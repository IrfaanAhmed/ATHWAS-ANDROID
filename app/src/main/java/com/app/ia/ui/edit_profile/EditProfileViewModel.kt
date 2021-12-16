package com.app.ia.ui.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityEditProfileBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.ProfileResponse
import com.app.ia.ui.change_password.ChangePasswordActivity
import com.app.ia.ui.otp_verify.OTPVerifyActivity
import com.app.ia.utils.*
import com.app.ia.utils.CommonUtils.isEmailValid
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityEditProfileBinding

    var userData = MutableLiveData<ProfileResponse>()
    var filePath = MutableLiveData<String>()
    private var oldMobileEmail = MutableLiveData<String>()

    fun setVariable(mBinding: ActivityEditProfileBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.edit_profile))
    }

    fun setIntent(intent: Intent) {
        userData.value = intent.getSerializableExtra(AppConstants.EXTRA_PROFILE_DETAIL) as ProfileResponse
    }

    fun onChangePasswordClick() {
        mActivity.startActivity<ChangePasswordActivity>()
    }

    fun updateUserName() {
        mBinding.edtTextName.setText(mBinding.edtTextName.text.toString().trim())
        val name = mBinding.edtTextName.text.toString()

        mBinding.tilTextName.error = null
        mBinding.tilTextName.isErrorEnabled = false


        when {
            name.length < 3 || name.length > 30 -> {
//                IADialog(mActivity, mActivity.getString(R.string.msg_name_validation_length), true)
                mBinding.tilTextName.error=mActivity.getString(R.string.msg_name_validation_length)
            }
            ValidationUtils.isHaveLettersOnly(name) ->{
//                IADialog(mActivity, mActivity.getString(R.string.name_valid_msg), true)
                mBinding.tilTextName.error=mActivity.getString(R.string.name_valid_msg)
            }
            else -> {
                val requestParams = HashMap<String, String>()
                requestParams["field_key"] = "username"
                requestParams["field_value"] = name
                setupObservers(false, requestParams)
                (mActivity as EditProfileActivity).hideKeyboard()
            }
        }
    }

    fun updateMobileNumber() {
        val mobileNumber = mBinding.edtTextMobile.text.toString()
        mBinding.tilTextMobile.error=null
        mBinding.tilTextMobile.isErrorEnabled=false
        if(!ValidationUtils.isValidPhone(mobileNumber)){
//            IADialog(mActivity, mActivity.getString(R.string.enter_valid_mobile_no), true)
            mBinding.tilTextMobile.error=mActivity.getString(R.string.enter_valid_mobile_no)
        }
        else{
            (mActivity as EditProfileActivity).hideKeyboard()
            oldMobileEmail.value = AppPreferencesHelper.getInstance().phone
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "new_phone"
            requestParams["field_value"] = mobileNumber
            setupObservers(true, requestParams)
        }
    }

    fun updateEmailAddress() {
        mBinding.edtTextEmail.setText(mBinding.edtTextEmail.text.toString().trim())
        val emailAddress = mBinding.edtTextEmail.text.toString()
        mBinding.tilTextEmail.error=null
        mBinding.tilTextEmail.isErrorEnabled=false
        if (emailAddress.length < 9 || emailAddress.length > 320) {
//            IADialog(mActivity, mActivity.getString(R.string.msg_email_validation_length), true)
            mBinding.tilTextEmail.error=mActivity.getString(R.string.msg_email_validation_length)
        }  else if (!ValidationUtils.isValidEmail(emailAddress)) {
//            IADialog(mActivity, mActivity.getString(R.string.enter_valid_email), true)
            mBinding.tilTextEmail.error=mActivity.getString(R.string.enter_valid_email)
        } else {
            oldMobileEmail.value = AppPreferencesHelper.getInstance().email
            val requestParams = HashMap<String, String>()
            requestParams["field_key"] = "new_email"
            requestParams["field_value"] = emailAddress
            setupObservers(true, requestParams)
            (mActivity as EditProfileActivity).hideKeyboard()
        }

    }

    fun updateProfileImage() {
        val selectedImageFile = CommonUtils.prepareFilePart(mActivity, "field_value", Uri.parse(filePath.value), File(filePath.value!!))

        val requestParams = HashMap<String, RequestBody>()
        requestParams["field_key"] = CommonUtils.prepareDataPart("user_image")
        setupObservers(requestParams, selectedImageFile)
    }

    private fun updateProfile(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.updateProfile(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun updateProfile(partData: Map<String, RequestBody>, file: MultipartBody.Part) =
        liveData(Dispatchers.Main) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = baseRepository.updateProfile(partData, file)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    private fun setupObservers(partData: Map<String, RequestBody>, file: MultipartBody.Part) {
        updateProfile(partData, file).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            val imgBitmap = BitmapFactory.decodeFile(filePath.value)
                            mBinding.profileImg.setImageBitmap(imgBitmap)
                            val localBroadCast = LocalBroadcastManager.getInstance(mActivity)
                            val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
                            localBroadCast.sendBroadcast(intent)
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun setupObservers(isVerify: Boolean, requestParams: HashMap<String, String>) {
        updateProfile(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            mBinding.tilTextName.error = null
                            mBinding.tilTextName.isErrorEnabled = false
                            mBinding.tilTextMobile.error = null
                            mBinding.tilTextMobile.isErrorEnabled = false
                            mBinding.tilTextEmail.error = null
                            mBinding.tilTextEmail.isErrorEnabled = false
                            if (isVerify) {
                                mActivity.startActivity<OTPVerifyActivity> {
                                    putExtra("countryCode", users.data?.countryCode)
                                    putExtra("mobileNumber", users.data?.phone)
                                    putExtra("otp", users.data?.otpNumber)
                                    putExtra("otpFor", users.data?.otpFor)
                                }
                            } else {
                                val localBroadCast = LocalBroadcastManager.getInstance(mActivity)
                                val intent = Intent(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
                                localBroadCast.sendBroadcast(intent)
                                //setupObservers()
                            }
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