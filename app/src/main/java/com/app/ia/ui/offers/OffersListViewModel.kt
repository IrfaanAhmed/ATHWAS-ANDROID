package com.app.ia.ui.offers

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityOfferListBinding
import com.app.ia.enums.Status
import com.app.ia.model.OffersResponse
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OffersListViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOfferListBinding

    val isItemAvailable = MutableLiveData(true)
    var isSearchTextEntered = MutableLiveData<Boolean>()

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    private val offerType = MutableLiveData("1")
    private val productId = MutableLiveData("")
    var promoCodeListData = MutableLiveData<MutableList<OffersResponse.Docs>>()
    var promoCodeList = ArrayList<OffersResponse.Docs>()


    var isLoading = true

    fun setVariable(mBinding: ActivityOfferListBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(intent.getStringExtra("offerName"))
        offerType.value = intent.getIntExtra("offerType", 1).toString()
        productId.value = intent.getStringExtra("product_id")
    }

    fun onClearClick() {
        isSearchTextEntered.value = false
        mBinding.edtTextOffers.setText("")
    }

    private fun offerList(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.offerList(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun offerListObserver(keyword: String) {

        val params = HashMap<String, String>()
        params["page_no"] = currentPage.value!!.toString()
        params["keyword"] = keyword
        params["offer_type"] = offerType.value!!
        params["product_id"] = productId.value!!

        var isLoading = true
        offerList(params).observe(mBinding.lifecycleOwner!!) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var isLoading = false
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            users.data?.docs?.let { it1 -> promoCodeList.addAll(it1?.filter { Date().time < it.endTimeInMills() }) }
                            promoCodeListData.value = promoCodeList

                            if (promoCodeListData.value?.size!! > 0) {
                                mBinding.recyclerViewOffers.visibility = View.VISIBLE
                                mBinding.noOffersText.visibility = View.GONE
                            } else {
                                mBinding.recyclerViewOffers.visibility = View.GONE
                                mBinding.noOffersText.visibility = View.VISIBLE
                            }
                        }
                    }

                    Status.ERROR -> {
                        var isLoading = false
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
        }
    }
}