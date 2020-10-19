package com.app.ia.ui.business_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.HomeCategoryListItemBinding
import com.app.ia.model.BusinessCategoryResponse
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class CategoryAdapter : ListAdapter<BusinessCategoryResponse.Docs, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<BusinessCategoryResponse.Docs>() {

        override fun areItemsTheSame(oldItem: BusinessCategoryResponse.Docs, newItem: BusinessCategoryResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BusinessCategoryResponse.Docs, newItem: BusinessCategoryResponse.Docs): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(HomeCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class CategoryViewHolder(private val mBinding: HomeCategoryListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(businessCategory: BusinessCategoryResponse.Docs) {

            mBinding.apply {
                category = businessCategory
                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductCategoryActivity> {
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_NAME, businessCategory.name)
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID, businessCategory._Id)
                    }
                }
                executePendingBindings()
            }
        }
    }
}