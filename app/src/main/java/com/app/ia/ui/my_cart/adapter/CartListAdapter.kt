package com.app.ia.ui.my_cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CartListRowBinding
import com.app.ia.model.CartListResponse
import com.app.ia.ui.my_cart.CartUpdateListener

class CartListAdapter(private val updateListener: CartUpdateListener) : ListAdapter<CartListResponse.Docs, CartListAdapter.CartViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<CartListResponse.Docs>() {

        override fun areItemsTheSame(oldItem: CartListResponse.Docs, newItem: CartListResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartListResponse.Docs, newItem: CartListResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(CartListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CartViewHolder(private val mBinding: CartListRowBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: CartListResponse.Docs, position: Int) {
            mBinding.apply {
                itemCategory = productItem.id.name
                val adapter = CartListItemAdapter(updateListener)
                recViewCartItem.adapter = adapter
                adapter.submitList(productItem.categoryItems)
                recViewCartItem.isNestedScrollingEnabled = false
                executePendingBindings()
            }
        }
    }

}