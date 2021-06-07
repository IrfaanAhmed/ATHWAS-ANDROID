package com.app.ia.ui.payment

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityStatusBinding
import com.app.ia.model.PaymentStatusResponse
import com.app.ia.ui.home.HomeActivity
import com.app.ia.utils.startActivityWithFinish

class StatusViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityStatusBinding
    var paymentStatusResponse = MutableLiveData<PaymentStatusResponse>()

    fun setVariable(mBinding: ActivityStatusBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("Payment Response")

        paymentStatusResponse.value = intent.getSerializableExtra("status") as PaymentStatusResponse

        mBinding.buttonContinue.setOnClickListener {
            if (paymentStatusResponse.value!!.requestType == 7) {
                mActivity.finish()
            } else {
                mActivity.startActivityWithFinish<HomeActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
    }
}