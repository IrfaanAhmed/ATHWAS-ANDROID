package com.app.ia.ui.my_order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.RecyclerViewPaginator
import kotlinx.android.synthetic.main.fragment_my_order.*
import kotlinx.android.synthetic.main.fragment_my_order.mSwipeRefresh

class MyOrdersFragment : BaseFragment<FragmentMyOrderBinding, MyOrdersViewModel>() {

    private var mFragmentMyOrderBinding: FragmentMyOrderBinding? = null
    private var mMyOrdersViewModel: MyOrdersViewModel? = null

    var orderAdapter: OrderListAdapter? = null
    private lateinit var recyclerViewPaging: RecyclerViewPaginator

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

        recyclerViewPaging = object : RecyclerViewPaginator(recViewOrder) {
            override val isLastPage: Boolean
                get() = mMyOrdersViewModel!!.isLastPage.value!!

            override fun loadMore(start: Int, count: Int) {
                mMyOrdersViewModel?.currentPage?.value = start
                mMyOrdersViewModel?.orderHistoryObserver()
            }
        }

        recViewOrder.addOnScrollListener(recyclerViewPaging)
        orderAdapter = OrderListAdapter()
        recViewOrder.adapter = orderAdapter

        mMyOrdersViewModel?.orderList?.observe(requireActivity(), {
            if (it.size <= 0) {
                orderAdapter?.notifyDataSetChanged()
            } else {
                if (orderAdapter?.currentList?.size!! == 0) {
                    orderAdapter?.submitList(it)
                } else {
                    orderAdapter?.notifyDataSetChanged()
                }
            }
        })

        mSwipeRefresh.setOnRefreshListener {
            if(mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = false
                resetOrderHistory()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_ORDER_STATUS && resultCode == AppCompatActivity.RESULT_OK) {
            val statusChanged = data?.getBooleanExtra("isStatusChanged", true)!!
            if (statusChanged) {
                resetOrderHistory()
            }
        }
    }

    fun resetOrderHistory() {
        mMyOrdersViewModel?.orderListAll?.clear()
        mMyOrdersViewModel?.currentPage?.value = 1
        mMyOrdersViewModel?.orderHistoryObserver()
    }
}