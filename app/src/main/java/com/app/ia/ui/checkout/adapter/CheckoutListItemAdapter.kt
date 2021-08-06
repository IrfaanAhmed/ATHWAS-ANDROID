package com.app.ia.ui.checkout.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CheckoutListItemBinding
import com.app.ia.model.CartListResponse
import com.app.ia.ui.checkout.CheckoutUpdateListener
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.cart_list_item.view.*

class CheckoutListItemAdapter(private val checkoutUpdateListener: CheckoutUpdateListener) : ListAdapter<CartListResponse.Docs.CategoryItems, CheckoutListItemAdapter.CheckoutViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<CartListResponse.Docs.CategoryItems>() {

        override fun areItemsTheSame(oldItem: CartListResponse.Docs.CategoryItems, newItem: CartListResponse.Docs.CategoryItems): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartListResponse.Docs.CategoryItems, newItem: CartListResponse.Docs.CategoryItems): Boolean {
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

        fun onBind(productItem: CartListResponse.Docs.CategoryItems, position: Int) {
            mBinding.apply {
                product = productItem
                executePendingBindings()
                itemView.tvActualPrice.paintFlags = itemView.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                ivDelete.setOnClickListener {
                    checkoutUpdateListener.onDeleteItem(productItem, position)
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> {
                        putExtra("product_id", productItem.inventoryId)
                    }
                }

                if(productItem.isAvailable == 0){
                    textStatus.text = "Not available"
                    layoutNotAvailable.visibility = View.VISIBLE
                    layoutQuantity.visibility = View.GONE
                }
                else if(productItem.availableQuantity <= 0){
                    textStatus.text = "Out of stock"
                    layoutNotAvailable.visibility = View.VISIBLE
                    layoutQuantity.visibility = View.GONE
                }
                else{
                    textStatus.text = ""
                    layoutNotAvailable.visibility = View.GONE
                    layoutQuantity.visibility = View.GONE
                }
            }
        }
    }

}