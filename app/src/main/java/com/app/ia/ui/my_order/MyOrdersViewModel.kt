package com.app.ia.ui.my_order

import android.app.Activity
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentMyOrderBinding
import com.app.ia.enums.Status
import com.app.ia.model.OrderHistoryResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class MyOrdersViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentMyOrderBinding

    val isItemAvailable = ObservableBoolean(true)
    val isCurrentOrder = MutableLiveData(true)

    var orderList = MutableLiveData<MutableList<OrderHistoryResponse.Docs>>()
    val orderListAll = ArrayList<OrderHistoryResponse.Docs>()

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: FragmentMyOrderBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        orderHistoryObserver()
    }

    fun onOrderTabsClick(value: Boolean) {
        isCurrentOrder.value = value
        orderListAll.clear()
        currentPage.value = 1
        orderHistoryObserver()
    }

    private fun orderHistory(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            if (isCurrentOrder.value!!) {
                emit(Resource.success(data = baseRepository.orderHistory(requestParams)))
            } else {
                emit(Resource.success(data = baseRepository.orderPastHistory(requestParams)))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun orderHistoryObserver() {

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage.value!!.toString()
        requestParams["limit"] = "20"

        orderHistory(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            orderListAll.addAll(users.data!!.docs)
                            orderList.value = orderListAll
                            isItemAvailable.set(orderListAll.size > 0)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
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