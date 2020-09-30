package com.app.ia.ui.notification

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityNotificationBinding
import com.app.ia.model.NotificationResponse

class NotificationViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    var notificationListData = MutableLiveData<MutableList<NotificationResponse.Docs>>()
    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityNotificationBinding

    fun setVariable(mBinding: ActivityNotificationBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        this.title.set(mActivity.getString(R.string.notifications))
    }

    /*private fun getNotifications(notification_id: String?, requestParams: HashMap<String, String>?, notificationType: Int) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            when (notificationType) {
                NotificationFragment.NOTIFICATION_LIST -> {
                    emit(Resource.success(data = baseRepository.getNotification(requestParams!!)))
                }
                NotificationFragment.DELETE_NOTIFICATION -> {
                    emit(Resource.success(data = baseRepository.deleteNotification(notification_id!!)))
                }
                NotificationFragment.DELETE_ALL_NOTIFICATION -> {
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
                                    NotificationFragment.NOTIFICATION_LIST -> {
                                        val response = users.data as NotificationResponse
                                        isLastPage.value = (currentPage.value == response.totalPages)
                                        notificationListData.plusAssign(response.docs)
                                        fragment.mNotificationAdapter.notifyDataSetChanged()
                                        showHide()
                                        (mActivity as HomeActivity).removeNotificationBadge()
                                    }

                                    NotificationFragment.DELETE_ALL_NOTIFICATION -> {
                                        val notificationList = notificationListData.value
                                        notificationList?.clear()
                                        notificationListData.value = notificationList
                                        fragment.mNotificationAdapter.notifyDataSetChanged()
                                        showHide()
                                    }

                                    NotificationFragment.DELETE_NOTIFICATION -> {
                                        val updatedList = notificationListData.value
                                        updatedList?.removeAt(deletedPosition)
                                        notificationListData.value = updatedList
                                        fragment.mNotificationAdapter.notifyItemRemoved(deletedPosition)
                                        fragment.mNotificationAdapter.notifyItemRangeChanged(deletedPosition, updatedList?.size!!)
                                        showHide()
                                    }

                                    else -> {
                                        TivoDialog(mActivity, users.message, true)
                                    }
                                }
                            } else {
                                TivoDialog(mActivity, users.message, true)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {

                    }
                }
            }
        })
    }*/

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