package com.app.ia.ui.my_order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OrderListItemBinding

class OrderListAdapter :
    ListAdapter<String, OrderListAdapter.OrderViewHolder>(
        OffersListDiffCallback()
    ) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }



    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class OrderViewHolder(private val mBinding: OrderListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(orderItem: String) {
            mBinding.apply {
                order = orderItem
                executePendingBindings()

               /* itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> ()
                }*/
            }
        }
    }

}