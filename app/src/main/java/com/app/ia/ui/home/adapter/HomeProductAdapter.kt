package com.app.ia.ui.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.HomeProductListItemBinding
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity

class HomeProductAdapter(private val isDiscountedProduct: Boolean) : ListAdapter<String, HomeProductAdapter.HomeProductViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onBindViewHolder(holder: HomeProductViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductViewHolder {
        return HomeProductViewHolder(HomeProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class HomeProductViewHolder(private val mBinding: HomeProductListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(favourite: String) {

            mBinding.apply {
                isDiscounted = isDiscountedProduct

                if (isDiscountedProduct) {
                    txtDiscountPrice.paintFlags = txtDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                itemView.setOnClickListener {
                    //itemView.context.startActivity<ProductDetailActivity>()
                }
                executePendingBindings()
            }
        }
    }
}