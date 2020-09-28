package com.app.ia.ui.my_cart

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityMyCartBinding

class MyCartViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityMyCartBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    fun setVariable(mBinding: ActivityMyCartBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.cart))
    }

}