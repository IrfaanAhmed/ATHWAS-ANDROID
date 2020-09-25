package com.app.ia.ui.business_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.HomeCategoryListItemBinding
import com.app.ia.model.BusinessCategoryBean
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class CategoryAdapter : ListAdapter<BusinessCategoryBean, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<BusinessCategoryBean>() {

        override fun areItemsTheSame(oldItem: BusinessCategoryBean, newItem: BusinessCategoryBean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BusinessCategoryBean, newItem: BusinessCategoryBean): Boolean {
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

        fun onBind(categoryItem: BusinessCategoryBean) {

            mBinding.apply {
                category = categoryItem
                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductCategoryActivity> {
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY, "Mobile")
                    }
                }
                executePendingBindings()
            }
        }
    }
}