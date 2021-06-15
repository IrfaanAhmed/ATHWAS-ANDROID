package com.app.ia.ui.my_cart.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CartListItemBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.CartListResponse
import com.app.ia.ui.my_cart.CartUpdateListener
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.cart_list_item.view.*

class CartListItemAdapter(private val updateListener: CartUpdateListener) : ListAdapter<CartListResponse.Docs.CategoryItems, CartListItemAdapter.CartListItemViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<CartListResponse.Docs.CategoryItems>() {

        override fun areItemsTheSame(oldItem: CartListResponse.Docs.CategoryItems, newItem: CartListResponse.Docs.CategoryItems): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartListResponse.Docs.CategoryItems, newItem: CartListResponse.Docs.CategoryItems): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: CartListItemViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListItemViewHolder {
        return CartListItemViewHolder(CartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CartListItemViewHolder(private val mBinding: CartListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: CartListResponse.Docs.CategoryItems, position: Int) {
            mBinding.apply {
                product = productItem
                executePendingBindings()
                itemView.tvActualPrice.paintFlags = itemView.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                var quantity = productItem.quantity
                ivMinus.setOnClickListener {
                    if (quantity == 1) {

                    } else {
                        quantity -= 1
                        itemView.tvQuantity.text = quantity.toString()
                        updateListener.onUpdate(productItem, quantity)
                    }
                }

                ivPlus.setOnClickListener {
                    if (quantity < productItem.getRemainingQuantity()) {
                        quantity += 1
                        itemView.tvQuantity.text = quantity.toString()
                        updateListener.onUpdate(productItem, quantity)
                    } else {
                        IADialog(itemView.context!! as Activity, "You cannot order more than " + productItem.getRemainingQuantity() + " units", true)
                    }
                }

                ivDelete.setOnClickListener {
                    updateListener.onDeleteItem(productItem, position)
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> {
                        putExtra("product_id", productItem.inventoryId)
                    }
                }
            }
        }
    }

}