package com.app.ia.ui.my_cart.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CartListItemBinding
import com.app.ia.databinding.ProductCategoryListItemBinding
import com.app.ia.databinding.ProductListItemBinding
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.cart_list_item.view.*

class CartListAdapter : ListAdapter<String, CartListAdapter.CartViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(CartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CartViewHolder(private val mBinding: CartListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: String) {
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

                itemView.ivPlus.setOnClickListener {
                    quantity += 1
                    itemView.tvQuantity.text = quantity.toString()
                }
            }
        }
    }

}