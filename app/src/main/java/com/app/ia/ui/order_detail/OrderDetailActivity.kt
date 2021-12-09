package com.app.ia.ui.order_detail

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.BR
import com.app.ia.IAApplication
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityOrderDetailBinding
import com.app.ia.model.OrderDetailResponse
import com.app.ia.ui.order_detail.adapter.OrderDetailListAdapter
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.common_header.view.*

class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding, OrderDetailViewModel>() {

    private var mBinding: ActivityOrderDetailBinding? = null
    var mViewModel: OrderDetailViewModel? = null
    private var orderDetailListAdapter: OrderDetailListAdapter? = null
    private var lastOrderTrackingStatus = ""
    var onReturnClicked = false

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun getViewModel(): OrderDetailViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!, intent)

        if (intent.getStringExtra("orderStatus") != null) {
            lastOrderTrackingStatus = intent.getStringExtra("orderStatus")!!
        }

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        toolbar.ivSearchIcon.setOnClickListener {
            startActivity<SearchActivity>()
        }

        orderDetailListAdapter = OrderDetailListAdapter()
        recyclerViewOrderList.adapter = orderDetailListAdapter

        mViewModel?.getOrderDetail()?.observe(this, {
            orderDetailListAdapter?.setOrderDateTime(it?.orderDate!!)
            if (it?.deliveredDate != null) {
                orderDetailListAdapter?.setDeliveryDateTime(it.deliveredDate)
            } else {
                orderDetailListAdapter?.setDeliveryDateTime("")
            }

            orderDetailListAdapter?.setOrderStatus(it?.orderStatus!!)
            if (it.category.isEmpty()) {
                orderDetailListAdapter?.notifyDataSetChanged()
            } else {
                if (orderDetailListAdapter?.currentList?.size!! == 0) {
                    orderDetailListAdapter?.submitList(it.category)
                } else {
                    orderDetailListAdapter?.submitList(it.category)
                }
                orderDetailListAdapter?.notifyDataSetChanged()
            }

            if (it?.orderStatus!! == 1 || it.orderStatus == 2 || it.orderStatus == 3) {
                buttonDownloadInvoice.visibility = View.VISIBLE
            } else {
                buttonDownloadInvoice.visibility = View.GONE
            }

            when (it.orderStatus) {
                3 -> {
                    txtViewOrderStatus.text = "Returned"
                    txtViewOrderStatus.setTextColor(Color.BLUE)
                    btnTrackOrder.visibility = View.GONE
                }

                4 -> {
                    txtViewOrderStatus.text = "Cancelled"
                    txtViewOrderStatus.setTextColor(Color.RED)
                    btnTrackOrder.visibility = View.GONE
                }

                else -> {
                    txtViewOrderStatus.text = it.currentTrackingStatus
                    txtViewOrderStatus.setTextColor(Color.parseColor("#a2c027"))
                    if (it.currentTrackingStatus.equals("Delivered", true)) {
                        btnTrackOrder.visibility = View.GONE
                    } else {
                        btnTrackOrder.visibility = View.VISIBLE
                    }
                }
            }
        })

        orderDetailListAdapter?.setOnItemSelectListener(object : OrderDetailListAdapter.OnItemSelectListener {

            override fun onOrderReturn(returnOrder: OrderDetailResponse.Category) {
                val params = HashMap<String, String>()
                params["order_id"] = mViewModel?.order_id?.value!!
                params["business_category_id"] = returnOrder.Id
                cancelledReasonDialog(params, isGroceryOrder = true, isReturn = true)
            }

            override fun onOrderCancel(cancelOrder: OrderDetailResponse.Category) {
                val params = HashMap<String, String>()
                params["order_id"] = mViewModel?.order_id?.value!!
                params["business_category_id"] = cancelOrder.Id
                mViewModel?.cancelReturnOrderObserver(params, isGroceryOrder = true, isReturn = false)
                //cancelledReasonDialog(params, isGroceryOrder = true, isReturn = false)
            }

            override fun onProductReturn(returnProduct: OrderDetailResponse.Category.Products) {
                val params = HashMap<String, String>()
                params["order_id"] = mViewModel?.order_id?.value!!
                params["sub_order_id"] = returnProduct.Id
                cancelledReasonDialog(params, isGroceryOrder = false, isReturn = true)
            }

            override fun onProductCancel(cancelProduct: OrderDetailResponse.Category.Products) {
                val params = HashMap<String, String>()
                params["order_id"] = mViewModel?.order_id?.value!!
                params["sub_order_id"] = cancelProduct.Id
                mViewModel?.cancelReturnOrderObserver(params, isGroceryOrder = false, isReturn = false)
                //cancelledReasonDialog(params, isGroceryOrder = false, isReturn = false)
            }
        })

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_REFRESH_ON_NOTIFICATION)
        localBroadcastReceiver.registerReceiver(refreshListener, intentFilter)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OrderDetailViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OrderDetailViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onStart() {
        super.onStart()
        refresh()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onRestart() {
        super.onRestart()
        IAApplication.getInstance().setCurrentActivity(this)
    }

    override fun onStop() {
        super.onStop()
        IAApplication.getInstance().setCurrentActivity(null)
    }

    private fun cancelledReasonDialog(params: HashMap<String, String>, isGroceryOrder: Boolean, isReturn: Boolean) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        if (window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.return_reason_dialog)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = (displayMetrics.heightPixels * 0.7).toInt()
        dialog.window!!.setLayout(width, height)
        dialog.show()
        val returnReason = dialog.findViewById<TextView>(R.id.txtViewReturnReason)
        val cancel = dialog.findViewById<FrameLayout>(R.id.imgViewCross)
        val submitBtn = dialog.findViewById<AppCompatButton>(R.id.btnSubmit)

        submitBtn.setOnClickListener {
            if (returnReason.text.toString().isEmpty()) {
                toast("Please enter return reason")
            } else {
                onReturnClicked = true
                if (dialog.isShowing) dialog.dismiss()
                params["reason"] = returnReason.text.toString()
                mViewModel?.cancelReturnOrderObserver(params, isGroceryOrder, isReturn)
            }
        }

        cancel.setOnClickListener {
            hideKeyboard()
            if (dialog.isShowing) dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_ORDER_STATUS && resultCode == RESULT_OK) {
            val statusChanged = data?.getBooleanExtra("isStatusChanged", true)!!
            if (statusChanged) {
                mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
            }
        }
    }

    private val refreshListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //CommonUtils.showNotificationCount(toolbar.txtNotificationCount)
            if (intent!!.getBooleanExtra("refresh", false)) {
                mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
            }
        }
    }

    fun refresh() {
        mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
        /*if (!onReturnClicked) {
            mViewModel?.orderDetailObserver(mViewModel?.order_id?.value!!)
        }*/
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isStatusChanged", (lastOrderTrackingStatus != mViewModel?.orderDetailResponse?.value?.currentTrackingStatus))
        setResult(RESULT_OK, intent)
        finish()
    }
}