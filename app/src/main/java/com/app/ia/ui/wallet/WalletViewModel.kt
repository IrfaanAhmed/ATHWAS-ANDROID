package com.app.ia.ui.wallet

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentWalletBinding
import com.app.ia.dialog.bottom_sheet_dialog.AddMoneyDialogFragment
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.PaymentOptionBean
import com.app.ia.model.WalletHistoryResponse
import com.app.ia.ui.home.HomeActivity
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class WalletViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentWalletBinding

    val isItemAvailable = MutableLiveData(true)
    var walletBalance = MutableLiveData("0")
    val walletListResponse = MutableLiveData<MutableList<WalletHistoryResponse.Docs>>()
    val walletListAll = ArrayList<WalletHistoryResponse.Docs>()

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: FragmentWalletBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        transactionHistoryObserver()
    }

    fun onAddMoneyClick() {
        val bottomSheetFragment = AddMoneyDialogFragment(getPaymentOptionList())
        bottomSheetFragment.setOnItemClickListener(object : AddMoneyDialogFragment.OnAddMoneyClickListener {
            override fun onAddMoneyClick(data: String) {
                walletBalance.value = CommonUtils.convertToDecimal(data)
                AppPreferencesHelper.getInstance().walletAmount = walletBalance.value!!
                currentPage.value = 1
                walletListAll.clear()
                transactionHistoryObserver()
            }
        })
        bottomSheetFragment.show((mActivity as HomeActivity).supportFragmentManager, "addMoney")
    }

    private fun getPaymentOptionList(): ArrayList<PaymentOptionBean> {
        val list = ArrayList<PaymentOptionBean>()
        list.add(PaymentOptionBean(mActivity.getString(R.string.credit_card), R.drawable.ic_credit_card, false))
        list.add(PaymentOptionBean(mActivity.getString(R.string.debit_card), R.drawable.ic_credit_card, true))
        list.add(PaymentOptionBean(mActivity.getString(R.string.netbanking), R.drawable.net_banking, false))
        return list
    }

    fun transactionHistoryObserver() {
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage.value!!.toString()
        requestParams["limit"] = "20"
        transactionHistoryObserver(requestParams)
    }

    private fun transactionHistory(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getTransactionHistory(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun transactionHistoryObserver(requestParams: HashMap<String, String>) {

        transactionHistory(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            walletListAll.addAll(users.data?.docs!!)
                            walletListResponse.value = walletListAll
                            walletBalance.value = CommonUtils.convertToDecimal(users.data?.userWallet!!.toString())
                            AppPreferencesHelper.getInstance().walletAmount = walletBalance.value!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}