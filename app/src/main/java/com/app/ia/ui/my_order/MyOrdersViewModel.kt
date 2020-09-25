package com.app.ia.ui.my_order

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentMyOrderBinding

class MyOrdersViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentMyOrderBinding

    val isItemAvailable = MutableLiveData(true)
    val isCurrentOrder = MutableLiveData(true)

    fun setVariable(mBinding: FragmentMyOrderBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun onOrderTabsClick(value: Boolean) {
        isCurrentOrder.value = value
    }

}