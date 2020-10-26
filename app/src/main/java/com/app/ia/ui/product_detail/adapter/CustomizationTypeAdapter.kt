package com.app.ia.ui.product_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.DetailCustomizationTypeBinding
import com.app.ia.model.ProductDetailResponse

class CustomizationTypeAdapter : ListAdapter<ProductDetailResponse.Product.Customizations, CustomizationTypeAdapter.CustomizationTypeViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<ProductDetailResponse.Product.Customizations>() {

        override fun areItemsTheSame(oldItem: ProductDetailResponse.Product.Customizations, newItem: ProductDetailResponse.Product.Customizations): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductDetailResponse.Product.Customizations, newItem: ProductDetailResponse.Product.Customizations): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: CustomizationTypeViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomizationTypeViewHolder {
        return CustomizationTypeViewHolder(DetailCustomizationTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CustomizationTypeViewHolder(private val mBinding: DetailCustomizationTypeBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(categoryItem: ProductDetailResponse.Product.Customizations, position: Int) {
            mBinding.apply {
                customization = categoryItem
                executePendingBindings()
                itemView.setOnClickListener {
                    if (onCustomizationClickListener != null) {
                        onCustomizationClickListener?.onCustomizationClick(categoryItem, position)
                    }
                }
            }
        }
    }

    private var onCustomizationClickListener: OnCustomizationClickListener? = null

    fun setOnCustomizationClickListener(onCustomizationClickListener: OnCustomizationClickListener) {
        this.onCustomizationClickListener = onCustomizationClickListener
    }

    interface OnCustomizationClickListener {
        fun onCustomizationClick(customization: ProductDetailResponse.Product.Customizations, position: Int)
    }

}