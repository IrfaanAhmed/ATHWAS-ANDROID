package com.app.ia.ui.search

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySearchBinding
import com.app.ia.databinding.ActivitySubCategoryBinding
import com.app.ia.model.ProductSubCategoryResponse

class SearchViewModel(baseRepository: BaseRepository) : BaseViewModel() {


    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySearchBinding

    //var productSubCategory = MutableLiveData<MutableList<ProductSubCategoryResponse.Docs>>()

    fun setVariable(mBinding: ActivitySearchBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }
}