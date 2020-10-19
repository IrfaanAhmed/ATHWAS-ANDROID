package com.app.ia.ui.delivery_address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.AddressListItemBinding

class DeliveryAddressAdapter : ListAdapter<String, DeliveryAddressAdapter.DeliveryAddressViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: DeliveryAddressViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryAddressViewHolder {
        return DeliveryAddressViewHolder(AddressListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class DeliveryAddressViewHolder(private val mBinding: AddressListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: String) {
            mBinding.apply {
                address = item
                executePendingBindings()
            }
        }
    }

}