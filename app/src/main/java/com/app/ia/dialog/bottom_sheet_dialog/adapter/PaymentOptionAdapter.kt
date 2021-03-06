package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.PaymentOptionListItemBinding
import com.app.ia.model.PaymentOptionBean

class PaymentOptionAdapter : ListAdapter<PaymentOptionBean, PaymentOptionAdapter.PaymentOptionViewHolder>(OffersListDiffCallback()) {

    private var onItemSelectListener: OnItemSelectListener? = null

    class OffersListDiffCallback : DiffUtil.ItemCallback<PaymentOptionBean>() {

        override fun areItemsTheSame(oldItem: PaymentOptionBean, newItem: PaymentOptionBean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PaymentOptionBean, newItem: PaymentOptionBean): Boolean {
            return oldItem.paymentOption == newItem.paymentOption
        }
    }

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    override fun onBindViewHolder(holder: PaymentOptionViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionViewHolder {
        return PaymentOptionViewHolder(
            PaymentOptionListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class PaymentOptionViewHolder(private val mBinding: PaymentOptionListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(paymentOptionItem: PaymentOptionBean) {
            mBinding.apply {
                paymentOption = paymentOptionItem
                executePendingBindings()

                itemView.setOnClickListener {
                    if (onItemSelectListener != null) {
                        onItemSelectListener?.onItemSelect(adapterPosition)
                    }
                }
            }
        }
    }

    interface OnItemSelectListener {
        fun onItemSelect(position: Int)
    }

}