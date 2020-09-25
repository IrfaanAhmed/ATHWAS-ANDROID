package com.app.ia.ui.wallet

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.*
import com.app.ia.dialog.bottom_sheet_dialog.AddMoneyDialogFragment
import com.app.ia.dialog.bottom_sheet_dialog.CommonSortDialogFragment
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.model.CommonSortBean
import com.app.ia.model.PaymentOptionBean
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.product_list.ProductListActivity
import com.app.ia.utils.AppConstants.EXTRA_PRODUCT_CATEGORY

class WalletViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentWalletBinding

    val isItemAvailable = MutableLiveData(true)

    fun setVariable(mBinding: FragmentWalletBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun onAddMoneyClick() {
        val bottomSheetFragment = AddMoneyDialogFragment(getPaymentOptionList())
        bottomSheetFragment.show((mActivity as HomeActivity).supportFragmentManager, bottomSheetFragment.tag)
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

    private fun getPaymentOptionList(): ArrayList<PaymentOptionBean> {
        val list = ArrayList<PaymentOptionBean>()
        list.add(PaymentOptionBean(mActivity.getString(R.string.credit_card), R.drawable.ic_credit_card, false))
        list.add(PaymentOptionBean(mActivity.getString(R.string.debit_card), R.drawable.ic_credit_card, true))
        list.add(PaymentOptionBean(mActivity.getString(R.string.netbanking), R.drawable.net_banking, false))
        return list
    }

}