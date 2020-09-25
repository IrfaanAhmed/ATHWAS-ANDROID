package com.app.ia.ui.offers

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentOffersBinding

class OffersViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentOffersBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: FragmentOffersBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

    }

}