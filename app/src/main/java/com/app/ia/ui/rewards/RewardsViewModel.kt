package com.app.ia.ui.rewards

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityRewardsBinding
import com.app.ia.enums.Status
import com.app.ia.model.OffersResponse
import com.app.ia.model.RedeemPointResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class RewardsViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityRewardsBinding

    val isItemAvailable = MutableLiveData(true)
    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    var promoCodeListData = MutableLiveData<MutableList<RedeemPointResponse.Docs>>()
    var promoCodeList = ArrayList<RedeemPointResponse.Docs>()

    fun setVariable(mBinding: ActivityRewardsBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.reward_points))
        redeemPointsObserver()
    }

    private fun redeemPoints(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.redeemPoints(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun redeemPointsObserver() {

        val params = HashMap<String, String>()
        params["page_no"] = currentPage.value!!.toString()

        redeemPoints(params).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            promoCodeList.addAll(users.data?.docs!!)
                            promoCodeListData.value = promoCodeList
                            isItemAvailable.value = promoCodeList.size > 0
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