package com.app.ia.ui.delivery_address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.AddressListItemBinding
import com.app.ia.model.AddressListResponse

class DeliveryAddressAdapter(private val previousAddressId: String) : ListAdapter<AddressListResponse.AddressList, DeliveryAddressAdapter.DeliveryAddressViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<AddressListResponse.AddressList>() {

        override fun areItemsTheSame(oldItem: AddressListResponse.AddressList, newItem: AddressListResponse.AddressList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AddressListResponse.AddressList, newItem: AddressListResponse.AddressList): Boolean {
            return oldItem == newItem
        }
    }

    private var onAddressClickListener: OnAddressClickListener? = null

    fun setOnAddressClickListener(onAddressClickListener: OnAddressClickListener) {
        this.onAddressClickListener = onAddressClickListener
    }


    override fun onBindViewHolder(holder: DeliveryAddressViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryAddressViewHolder {
        return DeliveryAddressViewHolder(AddressListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class DeliveryAddressViewHolder(private val mBinding: AddressListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: AddressListResponse.AddressList, position: Int) {
            mBinding.apply {
                address = item
                isDefaultAddress = if (previousAddressId.isEmpty()) item.defaultAddress == 1 else previousAddressId == item.Id

                itemView.setOnClickListener {
                    if (onAddressClickListener != null) {
                        onAddressClickListener?.onAddressSelect(item, position)
                    }
                }

                imgViewDelete.setOnClickListener {
                    if (onAddressClickListener != null) {
                        onAddressClickListener?.onAddressDelete(item, position)
                    }
                }

                textSetDefaultAddress.setOnClickListener {
                    if (onAddressClickListener != null) {
                        onAddressClickListener?.onSetDefaultAddress(item, position)
                    }
                }
                executePendingBindings()
            }
        }
    }

    interface OnAddressClickListener {

        fun onAddressSelect(item: AddressListResponse.AddressList, position: Int)
        fun onAddressDelete(item: AddressListResponse.AddressList, position: Int)
        fun onSetDefaultAddress(item: AddressListResponse.AddressList, position: Int)
    }

}