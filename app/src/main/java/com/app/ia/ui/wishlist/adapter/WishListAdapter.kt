package com.app.ia.ui.wishlist.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.WishListItemBinding
import com.app.ia.model.FavoriteListResponse

class WishListAdapter : ListAdapter<FavoriteListResponse.Docs, WishListAdapter.ProductViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<FavoriteListResponse.Docs>() {

        override fun areItemsTheSame(oldItem: FavoriteListResponse.Docs, newItem: FavoriteListResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FavoriteListResponse.Docs, newItem: FavoriteListResponse.Docs): Boolean {
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
        return ProductViewHolder(WishListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ProductViewHolder(private val mBinding: WishListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: FavoriteListResponse.Docs, position: Int) {
            mBinding.apply {
                product = productItem
                cutTextView.paintFlags = cutTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                /*if (productItem.isFavourite == 1) {
                    cbFavorite.setImageResource(R.drawable.ic_like)
                } else {
                    cbFavorite.setImageResource(R.drawable.ic_unlike)
                }*/
                cbFavorite.setOnClickListener {
                    onItemClickListener?.onFavoriteClick(productItem, position)
                }

                itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(productItem, position)
                }
                executePendingBindings()
            }
        }
    }


    interface OnItemClickListener {
        fun onFavoriteClick(productItem: FavoriteListResponse.Docs, position: Int)
        fun onItemClick(productItem: FavoriteListResponse.Docs, position: Int)
    }
}