package com.app.ia.ui.product_category

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductCategoryBinding
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers

class ProductCategoryViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductCategoryBinding
    var isProductCategoryAvailable = MutableLiveData(true)

    private val titleValue = MutableLiveData("")
    var productCategory = MutableLiveData<MutableList<ProductCategoryResponse.Docs>>()
    private var businessCategoryID = MutableLiveData<String>()

    fun setVariable(mBinding: ActivityProductCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        titleValue.value = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_NAME)
        businessCategoryID.value = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID)

        title.set(titleValue.value!!)
        productCategoryObserver()
    }

    private fun getProductCategory(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductCategory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun productCategoryObserver() {

        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = businessCategoryID.value!!

        getProductCategory(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            productCategory.value = users.data?.docs!!
                            // Store Category in shared preference.
                            val response: ProductCategoryResponse = users.data!!
                            val category = Gson().toJson(response)
                            AppPreferencesHelper.getInstance().setString(AppPreferencesHelper.CATEGORY, category)
                            isProductCategoryAvailable.value = users.data?.docs!!.size > 0
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if(!it.message.isNullOrEmpty()) {
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