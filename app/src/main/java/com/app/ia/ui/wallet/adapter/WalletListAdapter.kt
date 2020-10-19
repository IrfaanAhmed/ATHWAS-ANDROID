package com.app.ia.ui.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.WalletListItemBinding
import com.app.ia.model.WalletHistoryResponse

class WalletListAdapter : ListAdapter<WalletHistoryResponse.Docs, WalletListAdapter.WalletViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<WalletHistoryResponse.Docs>() {

        override fun areItemsTheSame(oldItem: WalletHistoryResponse.Docs, newItem: WalletHistoryResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WalletHistoryResponse.Docs, newItem: WalletHistoryResponse.Docs): Boolean {
            return oldItem.Id == newItem.Id
        }
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        return WalletViewHolder(WalletListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class WalletViewHolder(private val mBinding: WalletListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(walletItem: WalletHistoryResponse.Docs) {
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