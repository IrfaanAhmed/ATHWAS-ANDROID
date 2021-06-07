package com.app.ia.ui.filter

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityFilterBinding
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.CustomizationSubTypeResponse
import com.app.ia.model.CustomizationTypeResponse
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers

class FilterViewModel (private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityFilterBinding
    var customizationList = MutableLiveData<MutableList<CustomizationTypeResponse.Docs>>()
    var customizationSubTypeList = MutableLiveData<MutableList<CustomizationSubTypeResponse.CustomizationSubType>>()

    fun setVariable(mBinding: ActivityFilterBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        val categoryId = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID)!!
        customizationTypeObserver(categoryId)
    }

    private fun getCustomizationType(category_id : String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getCustomizationType(category_id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun customizationTypeObserver(category_id : String) {
        getCustomizationType(category_id).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            customizationList.value = users.data?.docs!!
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



    private fun getCustomizationSubType(customization_type_id : String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getCustomizationSubType(customization_type_id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun customizationSubTypeObserver(customization_type_id : String) {
        getCustomizationSubType(customization_type_id).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            customizationSubTypeList.value = users.data?.customizationSubType
                            val customizationSubType : CustomizationSubTypeResponse = users.data!!
                            val type = Gson().toJson(customizationSubType)
                            AppPreferencesHelper.getInstance().setString(customization_type_id, type)

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