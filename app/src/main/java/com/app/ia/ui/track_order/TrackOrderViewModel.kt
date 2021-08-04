package com.app.ia.ui.track_order

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityTrackOrderBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.OrderDetailResponse
import com.app.ia.ui.track.TrackOnMapActivity
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class TrackOrderViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityTrackOrderBinding
    val order_id = MutableLiveData("")

    var orderDetailResponse = MutableLiveData<OrderDetailResponse>()

    fun setVariable(mBinding: ActivityTrackOrderBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("Track Order")
        order_id.value = intent.getStringExtra("order_id")!!
//        orderDetailObserver(order_id.value!!)
    }

    private fun orderDetail(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.orderDetail(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun trackOnMap() {
        if (orderDetailResponse.value?.orderStatus == 2) {
            mActivity.toast("Order is delivered. can't track anymore.")
        } else {
            if (orderDetailResponse.value?.driver != null && orderDetailResponse.value?.driver?.size!! > 0) {
                val intent = Intent(getActivityNavigator()!!, TrackOnMapActivity::class.java)
                intent.putExtra("courier_id", "" + orderDetailResponse.value?.driver!![0].Id)
                intent.putExtra("rest_lat", orderDetailResponse.value?.deliveryAddress?.addressLocation?.coordinates!![1])
                intent.putExtra("rest_lng", orderDetailResponse.value?.deliveryAddress?.addressLocation?.coordinates!![0])
                mActivity.startActivity(intent)
            } else {
                mActivity.toast("Order has not been dispatched yet.")
            }
        }
    }

    fun onPhoneCall(phoneNumber: String) {
        val callDialog = IADialog(getActivityNavigator()!!, "", phoneNumber, "Call", "Cancel", false)
        callDialog.setOnItemClickListener(object : IADialog.OnClickListener {

            override fun onPositiveClick() {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                getActivityNavigator()?.startActivity(intent)
            }

            override fun onNegativeClick() {

            }
        })
    }

    fun orderDetailObserver(requestParams: String) {

        orderDetail(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            orderDetailResponse.value = users.data!!
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