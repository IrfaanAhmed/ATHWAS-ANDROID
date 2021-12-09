package com.app.ia.ui.track

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityTrackOnMapBinding
import com.app.ia.enums.Status
import com.app.ia.model.OrderDetailResponse
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class TrackOnMapViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityTrackOnMapBinding
    var orderDetailResponse = MutableLiveData<OrderDetailResponse>()

    fun setVariable(mBinding: ActivityTrackOnMapBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.track_order))
    }

    private fun orderDetail(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.orderDetail(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



    fun orderDetailObserver(requestParams: String) {

        orderDetail(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            orderDetailResponse.value = users.data!!
                            if (orderDetailResponse.value?.orderStatus == 2) {
                                mActivity.toast("Order is delivered. can't track anymore.")
                                mActivity.onBackPressed()
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
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