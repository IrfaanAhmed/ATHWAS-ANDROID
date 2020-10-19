package com.app.ia.ui.product_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.ProductCategoryListItemBinding
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class ProductCategoryListAdapter : ListAdapter<ProductCategoryResponse.Docs, ProductCategoryListAdapter.ProductCategoryViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<ProductCategoryResponse.Docs>() {

        override fun areItemsTheSame(oldItem: ProductCategoryResponse.Docs, newItem: ProductCategoryResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductCategoryResponse.Docs, newItem: ProductCategoryResponse.Docs): Boolean {
            return oldItem.name == newItem.name
        }
    }

   /* private var businessCategoryID = ""

    fun setBusinessCategoryID(businessCategoryID: String) {
        this.businessCategoryID = businessCategoryID
    }*/


    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder(ProductCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ProductCategoryViewHolder(private val mBinding: ProductCategoryListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(categoryItem: ProductCategoryResponse.Docs) {
            mBinding.apply {
                category = categoryItem
                executePendingBindings()

                itemView.setOnClickListener {
                    itemView.context.startActivity<SubCategoryActivity> {
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID, categoryItem.businessCategoryId._Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_NAME, categoryItem.name)
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID, categoryItem._Id)
                    }
                }
            }
        }
    }

}