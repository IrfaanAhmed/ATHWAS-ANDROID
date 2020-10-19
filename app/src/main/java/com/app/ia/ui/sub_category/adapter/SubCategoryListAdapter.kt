package com.app.ia.ui.sub_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.SubCategoryListItemBinding
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.model.ProductSubCategoryResponse
import com.app.ia.ui.product_list.ProductListActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class SubCategoryListAdapter : ListAdapter<ProductSubCategoryResponse.Docs, SubCategoryListAdapter.SubCategoryViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<ProductSubCategoryResponse.Docs>() {

        override fun areItemsTheSame(oldItem: ProductSubCategoryResponse.Docs, newItem: ProductSubCategoryResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductSubCategoryResponse.Docs, newItem: ProductSubCategoryResponse.Docs): Boolean {
            return oldItem.name == newItem.name
        }
    }


    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        return SubCategoryViewHolder(SubCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class SubCategoryViewHolder(private val mBinding: SubCategoryListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(categoryItem: ProductSubCategoryResponse.Docs) {
            mBinding.apply {
                category = categoryItem
                executePendingBindings()
                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductListActivity> {
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID, categoryItem.businessCategoryId._Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID, categoryItem.parentId)
                        putExtra(AppConstants.EXTRA_PRODUCT_SUB_CATEGORY_ID, categoryItem._Id)
                    }
                }
            }
        }
    }

}