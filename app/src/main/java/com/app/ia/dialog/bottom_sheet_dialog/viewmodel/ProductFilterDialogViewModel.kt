package com.app.ia.dialog.bottom_sheet_dialog.viewmodel

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseDialogViewModel
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.DialogProductFilterBinding
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.enums.Status
import com.app.ia.model.BrandResponse
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.model.ProductSubCategoryResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class ProductFilterDialogViewModel(private val baseRepository: BaseRepository) : BaseDialogViewModel() {

    lateinit var mActivity: Fragment
    lateinit var mBinding: DialogProductFilterBinding
    var productCategoryResponse = MutableLiveData<MutableList<ProductCategoryResponse.Docs>>()
    var productSubcategoryResponse = MutableLiveData<MutableList<ProductSubCategoryResponse.Docs>>()
    var brandListResponse = MutableLiveData<MutableList<BrandResponse.Docs>>()

    fun setVariable(mBinding: DialogProductFilterBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    private fun getProductListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductSubCategory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun productSubCategoryObserver(category_id: String) {
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = (mActivity as ProductFilterDialogFragment).businessCategoryId
        requestParams["category_id"] = category_id

        getProductListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            productSubcategoryResponse.value = users.data?.docs
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity.context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /***
     *  Brand List Observer
     */
    private fun brandListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getBrands(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun brandObserver() {
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = "1"

        brandListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            brandListResponse.value = users.data?.docs
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity.context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getCategoryListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductCategory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun productCategoryObserver() {
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = (mActivity as ProductFilterDialogFragment).businessCategoryId

        getCategoryListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            productCategoryResponse.value = users.data?.docs
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity.context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}