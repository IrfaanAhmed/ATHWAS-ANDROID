package com.app.ia.ui.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CheckoutRowBinding
import com.app.ia.model.CartListResponse
import com.app.ia.ui.checkout.CheckoutUpdateListener

class CheckoutAdapter(private val checkoutUpdateListener: CheckoutUpdateListener) : ListAdapter<CartListResponse.Docs, CheckoutAdapter.CheckoutViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<CartListResponse.Docs>() {

        override fun areItemsTheSame(oldItem: CartListResponse.Docs, newItem: CartListResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartListResponse.Docs, newItem: CartListResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        return CheckoutViewHolder(CheckoutRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CheckoutViewHolder(private val mBinding: CheckoutRowBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: CartListResponse.Docs, position: Int) {
            mBinding.apply {
                itemCategory = productItem.id.name
                val adapter = CheckoutListItemAdapter(checkoutUpdateListener)
                recViewCheckoutItem.adapter = adapter
                adapter.submitList(productItem.categoryItems)
                recViewCheckoutItem.isNestedScrollingEnabled = false
                executePendingBindings()
            }
        }
    }

}