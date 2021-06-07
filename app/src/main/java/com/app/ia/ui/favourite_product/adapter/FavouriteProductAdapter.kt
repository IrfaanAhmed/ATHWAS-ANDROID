package com.app.ia.ui.favourite_product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.FavouriteProductListItemBinding
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity

class FavouriteProductAdapter : ListAdapter<String, FavouriteProductAdapter.FavouriteProductViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: FavouriteProductViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteProductViewHolder {
        return FavouriteProductViewHolder(FavouriteProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class FavouriteProductViewHolder(private val mBinding: FavouriteProductListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: String) {
            mBinding.apply {
                product = productItem
                executePendingBindings()

                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity>()
                }
            }
        }
    }

}