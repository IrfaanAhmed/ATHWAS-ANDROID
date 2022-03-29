package com.app.ia.ui.offer_product_list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.OfferProductListItemBinding
import com.app.ia.databinding.ProductListItemBinding
import com.app.ia.model.OffersResponse
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.gone

class OfferProductListAdapter : ListAdapter<OffersResponse.Product, OfferProductListAdapter.ProductViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<OffersResponse.Product>() {

        override fun areItemsTheSame(oldItem: OffersResponse.Product, newItem: OffersResponse.Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OffersResponse.Product, newItem: OffersResponse.Product): Boolean {
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
        return ProductViewHolder(OfferProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ProductViewHolder(private val mBinding: OfferProductListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: OffersResponse.Product, position: Int) {
            mBinding.apply {
                product = productItem
                productData = productItem.productData[0]
                cutTextView.paintFlags = cutTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(productItem)
                }

                if (productItem.productData[0].isFavourite == 1) {
                    cbFavorite.setImageResource(R.drawable.ic_like)
                } else {
                    cbFavorite.setImageResource(R.drawable.ic_unlike)
                }

                cbFavorite.setOnClickListener {
                    onItemClickListener?.onFavoriteClick(productItem, position)
                }

                buttonAddToCart.setOnClickListener {
                    onItemClickListener?.onAddToCartClick(productItem)
                }
                cbFavorite.gone()
                layoutRatingOffer.gone()
                executePendingBindings()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(productItem: OffersResponse.Product)
        fun onAddToCartClick(productItem: OffersResponse.Product)
        fun onFavoriteClick(productItem: OffersResponse.Product, position: Int)
    }
}