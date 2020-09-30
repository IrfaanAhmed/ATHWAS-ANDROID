package com.app.ia.ui.product_list

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.dialog.bottom_sheet_dialog.CommonSortDialogFragment
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.model.CommonSortBean

class ProductListViewModel(private val baseRepository: BaseRepository) : BaseViewModel(),
    LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductListBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    fun setVariable(mBinding: ActivityProductListBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
    }

    fun onProductFilterClick() {
        val bottomSheetFragment = ProductFilterDialogFragment()
        bottomSheetFragment.setOnItemClickListener(object : ProductFilterDialogFragment.OnProductFilterClickListener {
            override fun onSubmitClick(filterValue: String) {

                /*mWalletViewModel?.filterBy?.value = filterValue
                mWalletViewModel?.walletResponseData?.removeObservers(this@WalletActivity)
                mWalletViewModel?.walletResponseData = MutableLiveData()
                walletAdapter = null
                callObserver()
                mWalletViewModel?.setupObservers()*/
            }

        })
        bottomSheetFragment.show(
            (mActivity as ProductListActivity).supportFragmentManager,
            bottomSheetFragment.tag
        )
    }

    fun onProductSortByClick() {
        val bottomSheetFragment = CommonSortDialogFragment(getProductSortList())
        bottomSheetFragment.show((mActivity as ProductListActivity).supportFragmentManager, bottomSheetFragment.tag)
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

    private fun getProductSortList(): ArrayList<CommonSortBean> {
        val list = ArrayList<CommonSortBean>()
        list.add(CommonSortBean(mActivity.getString(R.string.popularity), false))
        list.add(CommonSortBean(mActivity.getString(R.string.price_low_to_high), true))
        list.add(CommonSortBean(mActivity.getString(R.string.price_high_to_low), false))
        list.add(CommonSortBean(mActivity.getString(R.string.newest_first), false))
        return list
    }
}