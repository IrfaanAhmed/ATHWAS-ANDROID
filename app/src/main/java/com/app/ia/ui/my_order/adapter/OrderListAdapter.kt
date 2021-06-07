package com.app.ia.ui.my_order.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OrderListItemBinding
import com.app.ia.model.OrderHistoryResponse
import com.app.ia.ui.order_detail.OrderDetailActivity
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.mStartActivityForResult

class OrderListAdapter : ListAdapter<OrderHistoryResponse.Docs, OrderListAdapter.OrderViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderHistoryResponse.Docs>() {

        override fun areItemsTheSame(oldItem: OrderHistoryResponse.Docs, newItem: OrderHistoryResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderHistoryResponse.Docs, newItem: OrderHistoryResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(OrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OrderViewHolder(private val mBinding: OrderListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(orderItem: OrderHistoryResponse.Docs) {
            mBinding.apply {
                order = orderItem
                executePendingBindings()
                itemView.setOnClickListener {
                    (itemView.context as Activity).mStartActivityForResult<OrderDetailActivity>(AppRequestCode.REQUEST_ORDER_STATUS) {
                        putExtra("order_id", orderItem.Id)
                        putExtra("orderStatus", orderItem.currentTrackingStatus)
                    }
                }

                when (orderItem.orderStatus) {
                    3 -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.BLUE)
                        txtViewOrderStatus.text = "Status : Returned"
                        txtViewOrderStatus.setTextColor(Color.BLUE)
                    }

                    4 -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.RED)
                        txtViewOrderStatus.text = "Status : Cancelled"
                        txtViewOrderStatus.setTextColor(Color.RED)
                    }

                    else -> {
                        val statusGradient = txtViewOrderStatus.background as GradientDrawable
                        statusGradient.setStroke(2, Color.parseColor("#a2c027"))
                        txtViewOrderStatus.text = "Status : " + orderItem.currentTrackingStatus
                        txtViewOrderStatus.setTextColor(Color.parseColor("#a2c027"))
                    }
                }
            }
        }
    }
}