package com.app.ia.ui.business_category

import android.app.Activity
import androidx.lifecycle.*
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentCategoryBinding

class CategoryViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentCategoryBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: FragmentCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

    }


}