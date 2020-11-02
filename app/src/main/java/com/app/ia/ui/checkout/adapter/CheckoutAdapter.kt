package com.app.ia.ui.checkout.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CheckoutListItemBinding
import kotlinx.android.synthetic.main.cart_list_item.view.*

class CheckoutAdapter : ListAdapter<String, CheckoutAdapter.CheckoutViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        return CheckoutViewHolder(CheckoutListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CheckoutViewHolder(private val mBinding: CheckoutListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: String, position: Int) {
            mBinding.apply {
                product = productItem
                executePendingBindings()
                itemView.tvActualPrice.paintFlags = itemView.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                var quantity = 1
                itemView.ivMinus.setOnClickListener {
                    if (quantity == 1) {

                    } else {
                        quantity -= 1
                        itemView.tvQuantity.text = quantity.toString()
                    }
                }

                if(position % 2 == 0) {
                    txtViewHeader.visibility = View.VISIBLE
                } else {
                    txtViewHeader.visibility = View.GONE
                }

                itemView.ivPlus.setOnClickListener {
                    quantity += 1
                    itemView.tvQuantity.text = quantity.toString()
                }
            }
        }
    }

}