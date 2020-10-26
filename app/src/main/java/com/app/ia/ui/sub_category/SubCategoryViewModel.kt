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
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.ProductSubCategoryResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class SubCategoryViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySubCategoryBinding

    private val titleValue = MutableLiveData("")
    var productSubCategory = MutableLiveData<MutableList<ProductSubCategoryResponse.Docs>>()

    fun setVariable(mBinding: ActivitySubCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        titleValue.value = intent.extras!!.getString(AppConstants.EXTRA_PRODUCT_CATEGORY_NAME)
        title.set(titleValue.value!!)
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID)!!
        requestParams["category_id"] = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID)!!
        productCategoryObserver(requestParams)
    }

    private fun getProductCategory(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductSubCategory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun productCategoryObserver(requestParams: HashMap<String, String>) {

        getProductCategory(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {
                                productSubCategory.value = users.data?.docs!!
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