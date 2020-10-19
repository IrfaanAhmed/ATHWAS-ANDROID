package com.app.ia.ui.product_list.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.ProductListItemBinding
import com.app.ia.model.ProductListingResponse
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity

class ProductListAdapter : ListAdapter<ProductListingResponse.Docs, ProductListAdapter.ProductViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<ProductListingResponse.Docs>() {

        override fun areItemsTheSame(oldItem: ProductListingResponse.Docs, newItem: ProductListingResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductListingResponse.Docs, newItem: ProductListingResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ProductViewHolder(private val mBinding: ProductListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: ProductListingResponse.Docs, position: Int) {
            mBinding.apply {
                product = productItem
                cutTextView.paintFlags = cutTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(productItem)
                }

                if (productItem.isFavourite) {
                    cbFavorite.setImageResource(R.drawable.ic_like)
                } else {
                    cbFavorite.setImageResource(R.drawable.ic_unlike)
                }

                cbFavorite.setOnClickListener {
                    onItemClickListener?.onFavoriteClick(productItem, position)
                }
                executePendingBindings()
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(productItem: ProductListingResponse.Docs)
        fun onFavoriteClick(productItem: ProductListingResponse.Docs, position: Int)
    }
}