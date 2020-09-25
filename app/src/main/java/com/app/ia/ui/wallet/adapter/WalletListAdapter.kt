package com.app.ia.ui.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OrderListItemBinding
import com.app.ia.databinding.ProductCategoryListItemBinding
import com.app.ia.databinding.ProductListItemBinding
import com.app.ia.databinding.WalletListItemBinding
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class WalletListAdapter :
    ListAdapter<String, WalletListAdapter.WalletViewHolder>(
        OffersListDiffCallback()
    ) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }



    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        return WalletViewHolder(
            WalletListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class WalletViewHolder(private val mBinding: WalletListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(walletItem: String) {
            mBinding.apply {
                wallet = walletItem
                executePendingBindings()

                /*itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> ()
                }*/
            }
        }
    }

}