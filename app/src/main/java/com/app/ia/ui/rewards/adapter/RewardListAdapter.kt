package com.app.ia.ui.rewards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.*
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class RewardListAdapter : ListAdapter<String, RewardListAdapter.RewardViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        return RewardViewHolder(RewardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class RewardViewHolder(private val mBinding: RewardListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: String) {
            mBinding.apply {
                reward = item
                executePendingBindings()

                /*itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> ()
                }*/
            }
        }
    }

}