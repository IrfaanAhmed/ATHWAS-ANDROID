package com.app.ia.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentWalletBinding
import com.app.ia.dialog.bottom_sheet_dialog.AddMoneyDialogFragment
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.wallet.adapter.WalletListAdapter
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.RecyclerViewPaginator
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : BaseFragment<FragmentWalletBinding, WalletViewModel>() {

    private var mFragmentWalletBinding: FragmentWalletBinding? = null
    private var mWalletViewModel: WalletViewModel? = null

    private var walletAdapter: WalletListAdapter? = null

    private lateinit var recyclerViewPaging: RecyclerViewPaginator

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_wallet

    override val viewModel: WalletViewModel
        get() = mWalletViewModel!!

    companion object {
        fun newInstance(): WalletFragment {
            val args = Bundle()
            val fragment = WalletFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mWalletViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentWalletBinding = viewDataBinding!!
        mFragmentWalletBinding?.lifecycleOwner = this
        mWalletViewModel?.setVariable(mFragmentWalletBinding!!)

        recViewWallet.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))

        recyclerViewPaging = object : RecyclerViewPaginator(recViewWallet) {
            override val isLastPage: Boolean
                get() = mWalletViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mWalletViewModel?.currentPage?.value = start
                mWalletViewModel?.transactionHistoryObserver()
            }
        }
        recViewWallet.addOnScrollListener(recyclerViewPaging)
        walletAdapter = WalletListAdapter()
        recViewWallet.adapter = walletAdapter

        mWalletViewModel?.walletListResponse?.observe(viewLifecycleOwner, {
            if (it.size <= 0) {
                walletAdapter?.notifyDataSetChanged()
            } else {
                if (walletAdapter?.currentList?.size!! == 0) {
                    walletAdapter?.submitList(it)
                } else {
                    walletAdapter?.notifyDataSetChanged()
                }
            }
        })

        mSwipeRefresh.setOnRefreshListener {
            if (mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = false
            }
            mWalletViewModel?.currentPage?.value = 1
            mWalletViewModel?.walletListAll!!.clear()
            mWalletViewModel?.transactionHistoryObserver()
        }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(WalletViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mWalletViewModel = ViewModelProvider(this, factory).get(WalletViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).setupHeader(getString(R.string.my_wallet), false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_PAYMENT_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            if (parentFragmentManager.findFragmentByTag("addMoney") != null) {
                val cardDialog = parentFragmentManager.findFragmentByTag("addMoney") as AddMoneyDialogFragment
                cardDialog.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}