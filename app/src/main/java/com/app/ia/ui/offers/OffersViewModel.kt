package com.app.ia.ui.offers

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentOffersBinding
import com.app.ia.enums.Status
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class OffersViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentOffersBinding

    val isItemAvailable = MutableLiveData(true)

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: FragmentOffersBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }
}