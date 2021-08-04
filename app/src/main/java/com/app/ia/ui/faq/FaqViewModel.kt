package com.app.ia.ui.faq

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityFaqBinding
import com.app.ia.enums.Status
import com.app.ia.model.FaqResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers


class FaqViewModel(val baseRepository: BaseRepository) : BaseViewModel() {

    /**
     * Variable declaration
     */
    lateinit var mActivity: Activity
    var mBinding: ActivityFaqBinding? = null
    var titleType = ""
    var dataFound = MutableLiveData(true)
    var faqList = MutableLiveData<MutableList<FaqResponse.ContentData>>()

    var url = MutableLiveData("")
    var textTitle = MutableLiveData("")

    fun setVariable(mBinding: ActivityFaqBinding?) {
        this.mActivity = getActivityNavigator()!!
        this.mBinding = mBinding
    }

    fun setIntent(intent: Intent) {
        url.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_URL)!!
        textTitle.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_TITLE)!!
        title.set(textTitle.value)
        setupObservers(url.value!!)
    }


    private fun getContentData(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getFaqData(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(requestParams: String) {

        getContentData(requestParams).observe(mBinding?.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //mBinding.webview.loadData(users.data?.contentData!!, "text/html", "UTF-8")
                            faqList.value = users.data?.contentData!!
                            dataFound.value = faqList.value!!.size > 0
                        }
                    }

                    Status.ERROR -> {
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

    fun onRefresh() {
        if (mBinding?.refreshLayout?.isRefreshing!!) {
            mBinding?.refreshLayout?.isRefreshing = false
        }
        setupObservers(url.value!!)
    }

}
