package com.app.ia.ui.offers

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityOffersBinding

class OffersActivityViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityOffersBinding

    val isItemAvailable = MutableLiveData(true)

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: ActivityOffersBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.offers))
    }
}