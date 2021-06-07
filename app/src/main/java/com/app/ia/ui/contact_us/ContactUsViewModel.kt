package com.app.ia.ui.contact_us

import android.app.Activity
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityContactUsBinding
import com.app.ia.enums.Status
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class ContactUsViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityContactUsBinding

    fun setVariable(mBinding: ActivityContactUsBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.contact_us))
    }

    private fun userRegister(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            //emit(Resource.success(data = baseRepository.querySubmit(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun setupObservers(requestParams: HashMap<String, String>) {
        userRegister(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            /*if (users.status == "success") {
                                val IADialog = IADialog(mActivity, users.message, true)
                                IADialog.setOnItemClickListener(object : IADialog.OnClickListener {
                                    override fun onPositiveClick() {
                                        mActivity.finish()
                                    }

                                    override fun onNegativeClick() {
                                    }
                                })

                            } else {
                                IADialog(mActivity, users.message, true)
                            }*/
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

    fun onQuerySubmit() {

        val firstName = mBinding.edtTextFirstName.text.toString().trim()
        val lastName = mBinding.edtTextLastName.text.toString().trim()
        val email = mBinding.edtTextEmail.text.toString().trim()
        val phone = mBinding.edtTextMobile.text.toString().trim()
        val query = mBinding.edtTextQuery.text.toString().trim()

        /*if (firstName.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_first_name), true)
        } else if (firstName.length < 2) {
            IADialog(mActivity, "First name should be at least 2 characters", true)
        } else if (lastName.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_last_name), true)
        } else if (lastName.length < 2) {
            IADialog(mActivity, "Last name should be at least 2 characters", true)
        } else if (email.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_email), true)
        } else if (!email.isValidEmail()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_valid_email), true)
        } else if (phone.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.enter_your_mobile_no), true)
        } else if (query.isEmpty()) {
            IADialog(mActivity, mActivity.getString(R.string.please_enter_your_query), true)
        } else {

            val requestParams = HashMap<String, String>()
            requestParams["first_name"] = firstName
            requestParams["last_name"] = lastName
            requestParams["phone"] = phone
            requestParams["email"] = email
            requestParams["message"] = query
            setupObservers(requestParams)
        }*/
    }
}