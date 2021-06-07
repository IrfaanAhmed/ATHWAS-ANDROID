package com.app.ia.ui.track

import android.app.Activity
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityTrackOnMapBinding

class TrackOnMapViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityTrackOnMapBinding

    fun setVariable(mBinding: ActivityTrackOnMapBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.track_order))
    }
}