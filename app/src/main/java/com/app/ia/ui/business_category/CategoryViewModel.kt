package com.app.ia.ui.business_category

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentCategoryBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.BusinessCategoryResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentCategoryBinding

    val isItemAvailable = MutableLiveData(true)
    var businessCategory = MutableLiveData<MutableList<BusinessCategoryResponse.Docs>>()

    fun setVariable(mBinding: FragmentCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        businessCategoryObserver()
    }

    private fun getBusinessCategory() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getBusinessCategory()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun businessCategoryObserver() {

        getBusinessCategory().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {
                                businessCategory.value = users.data?.docs!!
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


}