package com.app.ia.ui.product_category

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductCategoryBinding
import com.app.ia.utils.AppConstants.EXTRA_PRODUCT_CATEGORY

class ProductCategoryViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductCategoryBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    fun setVariable(mBinding: ActivityProductCategoryBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

    }

    fun setIntent(intent: Intent) {
        titleValue.value = intent.extras!!.getString(EXTRA_PRODUCT_CATEGORY)
        title.set(titleValue.value!!)
    }

}