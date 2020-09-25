package com.app.ia.ui.place_picker

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityPlacePickerBinding

class PlacePickerViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityPlacePickerBinding

    val addressCategory = MutableLiveData(1)

    fun setVariable(mBinding: ActivityPlacePickerBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.add_new_address))
    }

    fun onAddressCategoryClick(value: Int){
        addressCategory.value = value
    }
}