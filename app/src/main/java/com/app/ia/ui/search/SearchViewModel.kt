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
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.AppConstants.EXTRA_VOICE_TEXT
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class SearchViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {


    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySearchBinding

    var productList = MutableLiveData<MutableList<ProductListingResponse.Docs>>()
    private val productListAll = ArrayList<ProductListingResponse.Docs>()

    var isSearchTextEntered = MutableLiveData<Boolean>()
    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    var voiceText = MutableLiveData("")

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
            mBinding.edtSearchText.setText(voiceText.value)
            setUpObserver(voiceText.value!!)
        }
    }

    fun onCancelClick() {
        mActivity.finish()
    }


    fun setUpObserver(keyword: String) {
        productListAll.clear()
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage.value!!.toString()
        requestParams["keyword"] = keyword
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

        getProductListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {
                                isItemAvailable.value = users.data?.docs!!.size > 0
                                isLastPage.value = (currentPage.value == users.data?.totalPages)
                                productListAll.addAll(users.data?.docs!!)
                                productList.value = productListAll
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