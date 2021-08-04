package com.app.ia.ui.sub_category

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySubCategoryBinding
import com.app.ia.enums.Status
import com.app.ia.model.ProductSubCategoryResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class SubCategoryViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySubCategoryBinding

    private val titleValue = MutableLiveData("")
    var productSubCategory = MutableLiveData<MutableList<ProductSubCategoryResponse.Docs>>()
    var isProductSubCategoryAvailable = MutableLiveData(true)

    private var businessCategoryID = MutableLiveData<String>()
    private var productCategoryID = MutableLiveData<String>()

    fun setVariable(mBinding: ActivitySubCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        titleValue.value = intent.extras!!.getString(AppConstants.EXTRA_PRODUCT_CATEGORY_NAME)
        title.set(titleValue.value!!)
        businessCategoryID.value = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID)!!
        productCategoryID.value = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID)!!
        productCategoryObserver()
    }

    private fun getProductCategory(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductSubCategory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun productCategoryObserver() {

        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = businessCategoryID.value!!
        requestParams["category_id"] = productCategoryID.value!!

        getProductCategory(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            productSubCategory.value = users.data?.docs!!
                            isProductSubCategoryAvailable.value = users.data?.docs!!.size > 0
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

}