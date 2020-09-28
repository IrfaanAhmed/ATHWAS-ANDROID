package com.app.ia.ui.rewards

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityRewardsBinding

class RewardsViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityRewardsBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: ActivityRewardsBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.reward_points))
    }

}