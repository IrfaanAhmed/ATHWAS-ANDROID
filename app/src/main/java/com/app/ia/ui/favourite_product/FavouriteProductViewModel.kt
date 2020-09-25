package com.app.ia.ui.favourite_product

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityFavouriteProductBinding

class FavouriteProductViewModel(private val baseRepository: BaseRepository) : BaseViewModel(),
    LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityFavouriteProductBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: ActivityFavouriteProductBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.favourite_product))
    }

}