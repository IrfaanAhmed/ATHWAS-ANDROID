package com.app.ia.ui.webview

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityWebViewBinding
import com.app.ia.enums.Status
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class WebViewViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityWebViewBinding

    var url = MutableLiveData("")
    var textTitle = MutableLiveData("")

    fun setVariable(mBinding: ActivityWebViewBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        url.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_URL)!!
        textTitle.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_TITLE)!!
        title.set(textTitle.value)
    }

    private fun getContentData(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getContentData(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(requestParams: String) {

        getContentData(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mBinding.webview.loadData(users.data?.contentData!!, "text/html", "UTF-8")
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
    }

}