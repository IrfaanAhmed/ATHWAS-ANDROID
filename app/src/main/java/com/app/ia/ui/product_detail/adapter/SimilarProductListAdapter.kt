package com.app.ia.ui.product_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.SimilarProductListItemBinding
import com.app.ia.model.SimilarProductListResponse

class SimilarProductListAdapter : ListAdapter<SimilarProductListResponse.Docs, SimilarProductListAdapter.SimilarProductViewHolder>(
    OffersListDiffCallback()
) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<SimilarProductListResponse.Docs>() {

        override fun areItemsTheSame(oldItem: SimilarProductListResponse.Docs, newItem: SimilarProductListResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SimilarProductListResponse.Docs, newItem: SimilarProductListResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: SimilarProductViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarProductViewHolder {
        return SimilarProductViewHolder(SimilarProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class SimilarProductViewHolder(private val mBinding: SimilarProductListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(categoryItem: SimilarProductListResponse.Docs, position: Int) {
            mBinding.apply {
                similarProduct = categoryItem
                executePendingBindings()

                itemView.setOnClickListener {
                    if (onSimilarProductClickListener != null) {
                        onSimilarProductClickListener?.onSimilarProductClick(categoryItem, position)
                    }
                }
            }
        }
    }

    private var onSimilarProductClickListener: OnSimilarProductClickListener? = null

    fun setOnSimilarProductClickListener(onSimilarProductClickListener: OnSimilarProductClickListener) {
        this.onSimilarProductClickListener = onSimilarProductClickListener
    }

    interface OnSimilarProductClickListener {
        fun onSimilarProductClick(customization: SimilarProductListResponse.Docs, position: Int)
    }

}