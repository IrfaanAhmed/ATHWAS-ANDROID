package com.app.ia.ui.rewards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.RewardListItemBinding
import com.app.ia.model.RedeemPointResponse

class RewardListAdapter : ListAdapter<RedeemPointResponse.Docs, RewardListAdapter.RewardViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<RedeemPointResponse.Docs>() {

        override fun areItemsTheSame(oldItem: RedeemPointResponse.Docs, newItem: RedeemPointResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RedeemPointResponse.Docs, newItem: RedeemPointResponse.Docs): Boolean {
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

        fun onBind(item: RedeemPointResponse.Docs) {
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