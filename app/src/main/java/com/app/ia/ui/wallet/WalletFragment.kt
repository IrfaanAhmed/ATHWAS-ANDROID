package com.app.ia.ui.wallet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentWalletBinding
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.wallet.adapter.WalletListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : BaseFragment<FragmentWalletBinding, WalletViewModel>() {

    private var mFragmentWalletBinding: FragmentWalletBinding? = null
    private var mWalletViewModel: WalletViewModel? = null

    private var walletAdapter: WalletListAdapter? = null

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

        walletAdapter = WalletListAdapter()
        recViewWallet.adapter = walletAdapter
        mWalletViewModel?.walletListResponse?.observe(viewLifecycleOwner, {
            walletAdapter!!.submitList(it)
        })
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
}