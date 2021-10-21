package com.app.ia.ui.search

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySearchBinding
import com.app.ia.enums.Status
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.AppConstants.EXTRA_VOICE_TEXT
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers

class SearchViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {


    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySearchBinding

    var productList = MutableLiveData<MutableList<ProductListingResponse.Docs>>()
    val productListAll = ArrayList<ProductListingResponse.Docs>()

    var isSearchTextEntered = MutableLiveData<Boolean>()
    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    private var voiceText = MutableLiveData("")

    var isLoading = true

    fun setVariable(mBinding: ActivitySearchBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.search))
    }

    fun onClearClick() {
        isSearchTextEntered.value = false
        mBinding.edtSearchText.setText("")
    }

    fun setIntent(intent: Intent) {
        voiceText.value = intent.getStringExtra(EXTRA_VOICE_TEXT)

        if (voiceText.value != null) {
            (mActivity as SearchActivity).keyword = voiceText.value!!
            mBinding.edtSearchText.setText(voiceText.value)
            setUpObserver(voiceText.value!!)
        }
    }

    fun onCancelClick() {
        mActivity.finish()
    }


    fun setUpObserver(keyword: String) {
        //productListAll.clear()
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage.value!!.toString()
        requestParams["keyword"] = keyword
        requestParams["filter"] = JsonArray().toString()
        productListingObserver(requestParams)
    }

    private fun getProductListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductListing(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun productListingObserver(requestParams: HashMap<String, String>) {
        isLoading = true
        getProductListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            isItemAvailable.value = users.data?.docs!!.size > 0
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            productListAll.addAll(users.data?.docs!!)
                            productList.value = productListAll
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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