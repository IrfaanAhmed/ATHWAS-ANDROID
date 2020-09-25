package com.app.ia.ui.product_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.ProductCategoryListItemBinding
import com.app.ia.model.BusinessCategoryBean
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class ProductCategoryListAdapter : ListAdapter<BusinessCategoryBean, ProductCategoryListAdapter.ProductCategoryViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<BusinessCategoryBean>() {

        override fun areItemsTheSame(oldItem: BusinessCategoryBean, newItem: BusinessCategoryBean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BusinessCategoryBean, newItem: BusinessCategoryBean): Boolean {
            return oldItem.name == newItem.name
        }
    }


    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder(ProductCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ProductCategoryViewHolder(private val mBinding: ProductCategoryListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(categoryItem: BusinessCategoryBean) {
            mBinding.apply {
                category = categoryItem
                executePendingBindings()

                itemView.setOnClickListener {
                    itemView.context.startActivity<SubCategoryActivity> {
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY, categoryItem.name)
                    }
                }
            }
        }
    }

}