package com.app.ia.ui.my_order

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentMyOrderBinding
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.my_order.adapter.OrderListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_my_order.*

class MyOrdersFragment : BaseFragment<FragmentMyOrderBinding, MyOrdersViewModel>() {

    private var mFragmentMyOrderBinding: FragmentMyOrderBinding? = null
    private var mMyOrdersViewModel: MyOrdersViewModel? = null

    var orderAdapter: OrderListAdapter? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_my_order

    override val viewModel: MyOrdersViewModel
        get() = mMyOrdersViewModel!!

    companion object {
        fun newInstance(): MyOrdersFragment {
            val args = Bundle()
            val fragment = MyOrdersFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mMyOrdersViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentMyOrderBinding = viewDataBinding!!
        mFragmentMyOrderBinding?.lifecycleOwner = this
        mMyOrdersViewModel?.setVariable(mFragmentMyOrderBinding!!)

        recViewOrder.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewOrder.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        orderAdapter = OrderListAdapter()
        recViewOrder.adapter = orderAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        categoryList.add("iPhone")
        categoryList.add("Motorola")
        categoryList.add("RealMe")
        categoryList.add("MI")
        orderAdapter!!.submitList(categoryList)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(MyOrdersViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mMyOrdersViewModel = ViewModelProvider(this, factory).get(MyOrdersViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).setupHeader(getString(R.string.my_orders), false)
        }
    }
}