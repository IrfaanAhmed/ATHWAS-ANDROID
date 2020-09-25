package com.app.ia.ui.product_detail

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.dialog.bottom_sheet_dialog.CustomisationDialogFragment
import com.app.ia.model.CustomisationBean

class ProductDetailViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductDetailBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    fun setVariable(mBinding: ActivityProductDetailBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
    }

    fun onCustomisationClick() {
        val bottomSheetFragment = CustomisationDialogFragment(getCustomisationList())
        bottomSheetFragment.show(
            (mActivity as ProductDetailActivity).supportFragmentManager,
            bottomSheetFragment.tag
        )
        /*bottomSheetFragment.setOnItemClickListener(object: ProductFilterDialogFragment.OnProductFilterClickListener{
            override fun onSubmitClick(filterValue: String) {

                *//*mWalletViewModel?.filterBy?.value = filterValue
                    mWalletViewModel?.walletResponseData?.removeObservers(this@WalletActivity)
                    mWalletViewModel?.walletResponseData = MutableLiveData()
                    walletAdapter = null
                    callObserver()
                    mWalletViewModel?.setupObservers()*//*
                }

            })*/

    }

    private fun getCustomisationList(): ArrayList<CustomisationBean>{
        var list = ArrayList<CustomisationBean>()
        list.add(CustomisationBean("2 kg", false))
        list.add(CustomisationBean("5 kg", true))
        list.add(CustomisationBean("10 kg",false))
        list.add(CustomisationBean("15 kg",false))
        list.add(CustomisationBean("20 kg",false))
        list.add(CustomisationBean("25 kg",false))
        list.add(CustomisationBean("30 kg",false))

        return list
    }
}