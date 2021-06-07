package com.app.ia.ui.order_detail.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OrderDetailListRowBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.OrderDetailResponse
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailListAdapter : ListAdapter<OrderDetailResponse.Category, OrderDetailListAdapter.OrderDetailViewHolder>(OffersListDiffCallback()) {

    private var orderDate = ""
    private var deliveryDate = ""
    private var orderStatus = 0

    fun setOrderDateTime(orderDate: String) {
        this.orderDate = orderDate
    }

    fun setDeliveryDateTime(deliveryDate: String) {
        this.deliveryDate = deliveryDate
    }

    fun setOrderStatus(orderStatus: Int) {
        this.orderStatus = orderStatus
    }

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderDetailResponse.Category>() {

        override fun areItemsTheSame(oldItem: OrderDetailResponse.Category, newItem: OrderDetailResponse.Category): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderDetailResponse.Category, newItem: OrderDetailResponse.Category): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder(OrderDetailListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OrderDetailViewHolder(private val mBinding: OrderDetailListRowBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: OrderDetailResponse.Category, position: Int) {
            mBinding.apply {
                products = if (productItem.products.isNotEmpty()) {
                    productItem.products[0]
                } else {
                    null
                }

                itemCategory = productItem.name
                isAllReturn = productItem.allReturn == 1
                isGroceryCancelled = (productItem.allReturn == 1 && (productItem.products.isNotEmpty() && productItem.products[0].orderStatus == 4))
                isItemReturned = (productItem.allReturn == 1 && (productItem.products.isNotEmpty() && (productItem.products[0].orderStatus == 2 || productItem.products[0].orderStatus == 3 || productItem.products[0].orderStatus == 1)))

                val showCancelVisibility: Boolean
                val showReturnVisibility: Boolean
                if (orderStatus == 2) {
                    showCancelVisibility = false
                    showReturnVisibility = returnVisibility(productItem.returnTime)
                } else {
                    showReturnVisibility = false
                    showCancelVisibility = cancelVisibility(productItem.cancellationTime)
                }

                isReturnAvailable = showReturnVisibility
                isCancelAvailable = showCancelVisibility

                btnOrderReturn.setOnClickListener {
                    val returnDialog = IADialog(itemView.context as Activity, "Do you want to return these items?", false)
                    returnDialog.setOnItemClickListener(object : IADialog.OnClickListener {
                        override fun onPositiveClick() {
                            if (onItemSelectListener != null) {
                                onItemSelectListener?.onOrderReturn(productItem)
                            }
                        }

                        override fun onNegativeClick() {

                        }
                    })
                }

                btnOrderCancel.setOnClickListener {

                    val returnDialog = IADialog(itemView.context as Activity, "Do you want to cancel these items?", false)
                    returnDialog.setOnItemClickListener(object : IADialog.OnClickListener {
                        override fun onPositiveClick() {
                            if (onItemSelectListener != null) {
                                onItemSelectListener?.onOrderCancel(productItem)
                            }
                        }

                        override fun onNegativeClick() {

                        }
                    })
                }

                val adapter = OrderDetailListItemAdapter(productItem.allReturn == 1, showCancelVisibility, showReturnVisibility, orderStatus)
                recViewOrderDetailItem.adapter = adapter
                adapter.submitList(productItem.products)
                recViewOrderDetailItem.isNestedScrollingEnabled = false
                adapter.setOnItemSelectListener(object : OrderDetailListItemAdapter.OnItemSelectListener {
                    override fun onProductReturn(returnProduct: OrderDetailResponse.Category.Products, position: Int) {
                        if (onItemSelectListener != null) {
                            onItemSelectListener?.onProductReturn(returnProduct)
                        }
                    }

                    override fun onProductCancel(cancelProduct: OrderDetailResponse.Category.Products, position: Int) {
                        if (onItemSelectListener != null) {
                            onItemSelectListener?.onProductCancel(cancelProduct)
                        }
                    }
                })
                executePendingBindings()
            }
        }
    }

    private var onItemSelectListener: OnItemSelectListener? = null

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    interface OnItemSelectListener {
        fun onOrderReturn(returnOrder: OrderDetailResponse.Category)
        fun onOrderCancel(cancelOrder: OrderDetailResponse.Category)
        fun onProductReturn(returnProduct: OrderDetailResponse.Category.Products)
        fun onProductCancel(cancelProduct: OrderDetailResponse.Category.Products)
    }


    fun returnVisibility(returnTime: Int): Boolean {
        if (deliveryDate.isEmpty()) {
            return false
        }
        val orderDate = getDeliveredDate()
        val finalReturnTime = addMinutesToDate(returnTime, orderDate)
        if (finalReturnTime.before(Date())) {
            return false
        }
        return true
    }

    fun cancelVisibility(cancelTime: Int): Boolean {
        val orderDate = getOrderDate()
        val finalCancelTime = addMinutesToDate(cancelTime, orderDate)
        if (finalCancelTime.before(Date())) {
            return false
        }
        return true
    }

    private fun getOrderDate(): Date {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: Date
        outputDate = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(orderDate)!!
        } catch (e: Exception) {
            Date()
        }
        return outputDate
    }

    private fun getDeliveredDate(): Date {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: Date
        outputDate = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(deliveryDate)!!
        } catch (e: Exception) {
            Date()
        }
        return outputDate
    }

    private fun addMinutesToDate(minutes: Int, beforeTime: Date): Date {
        val ONE_MINUTE_IN_MILLIS: Long = 60000 //millisecs
        val curTimeInMs = beforeTime.time
        return Date(curTimeInMs + minutes * ONE_MINUTE_IN_MILLIS)
    }


}