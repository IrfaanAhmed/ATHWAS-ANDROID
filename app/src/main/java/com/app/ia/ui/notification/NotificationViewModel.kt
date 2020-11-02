package com.app.ia.ui.notification

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityNotificationBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.NotificationResponse
import com.app.ia.utils.Resource
import com.app.ia.utils.plusAssign
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class NotificationViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    var notificationListData = MutableLiveData<MutableList<NotificationResponse.Docs>>()
    lateinit var mActivity: NotificationActivity
    lateinit var mBinding: ActivityNotificationBinding

    fun setVariable(mBinding: ActivityNotificationBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!! as NotificationActivity
        this.title.set(mActivity.getString(R.string.notifications))
    }

    private fun getNotifications(notification_id: String?, requestParams: HashMap<String, String>?, notificationType: Int) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            when (notificationType) {
                NotificationActivity.NOTIFICATION_LIST -> {
                    emit(Resource.success(data = baseRepository.getNotification(requestParams!!)))
                }
                NotificationActivity.DELETE_NOTIFICATION -> {
                    emit(Resource.success(data = baseRepository.deleteNotification(notification_id!!)))
                }
                NotificationActivity.DELETE_ALL_NOTIFICATION -> {
                    emit(Resource.success(data = baseRepository.deleteAllNotification()))
                }
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(notification_id: String?, requestParams: HashMap<String, String>?, notificationType: Int, deletedPosition: Int) {

        getNotifications(notification_id, requestParams, notificationType).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {

                                when (notificationType) {
                                    NotificationActivity.NOTIFICATION_LIST -> {
                                        val response = users.data as NotificationResponse
                                        isLastPage.value = (currentPage.value == response.totalPages)
                                        notificationListData.plusAssign(response.docs)
                                        mActivity.mNotificationAdapter.notifyDataSetChanged()
                                        showHide()
                                    }

                                    NotificationActivity.DELETE_ALL_NOTIFICATION -> {
                                        val notificationList = notificationListData.value
                                        notificationList?.clear()
                                        notificationListData.value = notificationList
                                        mActivity.mNotificationAdapter.notifyDataSetChanged()
                                        showHide()
                                    }

                                    NotificationActivity.DELETE_NOTIFICATION -> {
                                        val updatedList = notificationListData.value
                                        updatedList?.removeAt(deletedPosition)
                                        notificationListData.value = updatedList
                                        mActivity.mNotificationAdapter.notifyItemRemoved(deletedPosition)
                                        mActivity.mNotificationAdapter.notifyItemRangeChanged(deletedPosition, updatedList?.size!!)
                                        showHide()
                                    }

                                    else -> {
                                        IADialog(mActivity, users.message, true)
                                    }
                                }
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun showHide() {
        if (notificationListData.value?.size!! > 0) {
            mBinding.notificationRecyclerView.visibility = View.VISIBLE
            mBinding.noNotificationAvailableText.visibility = View.GONE
        } else {
            mBinding.notificationRecyclerView.visibility = View.GONE
            mBinding.noNotificationAvailableText.visibility = View.VISIBLE
        }
    }
}