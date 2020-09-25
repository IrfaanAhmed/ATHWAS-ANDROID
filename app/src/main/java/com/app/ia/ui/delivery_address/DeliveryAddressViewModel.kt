package com.app.ia.ui.delivery_address

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityDeliveryAddressBinding
import com.app.ia.ui.place_picker.PlacePickerActivity
import com.app.ia.utils.startActivity

class DeliveryAddressViewModel(private val baseRepository: BaseRepository) : BaseViewModel(),
    LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityDeliveryAddressBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    fun setVariable(mBinding: ActivityDeliveryAddressBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.choose_address))
    }

    fun onAddAddressClick(){
        mActivity.startActivity<PlacePickerActivity>()
    }
}