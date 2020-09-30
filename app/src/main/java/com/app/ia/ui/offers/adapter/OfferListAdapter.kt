package com.app.ia.ui.offers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.OfferListItemBinding

class OfferListAdapter : ListAdapter<String, OfferListAdapter.OfferViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        return OfferViewHolder(OfferListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class OfferViewHolder(private val mBinding: OfferListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(profileItem: String, position: Int) {
            mBinding.apply {

                when (position) {
                    0 -> {
                        imgView.setImageResource(R.drawable.offer1)
                        txtView.text = "Promo code offer"
                    }
                    1 -> {
                        imgView.setImageResource(R.drawable.offer2)
                        txtView.text = "Bundle offer"
                    }
                    2 -> {
                        imgView.setImageResource(R.drawable.offer3)
                        txtView.text = "Promotional offer"
                    }
                    3 -> {
                        imgView.setImageResource(R.drawable.offer4)
                        txtView.text = "Bank Offer"
                    }
                    4 -> {
                        imgView.setImageResource(R.drawable.offer3)
                        txtView.text = "Product Offer"
                    }
                }
                executePendingBindings()
            }
        }
    }
}