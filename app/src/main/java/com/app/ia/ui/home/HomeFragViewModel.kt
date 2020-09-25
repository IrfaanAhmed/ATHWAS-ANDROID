package com.app.ia.ui.home

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentHomeBinding

class HomeFragViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentHomeBinding

    fun setVariable(mBinding: FragmentHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun setHeader() {
        isHomeScreen.set(true)
    }
}