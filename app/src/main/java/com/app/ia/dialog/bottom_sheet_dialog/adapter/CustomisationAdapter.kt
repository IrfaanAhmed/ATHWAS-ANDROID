package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CustomisationListItemBinding
import com.app.ia.model.CustomizationProductDetail

class CustomisationAdapter : ListAdapter<CustomizationProductDetail.Data, CustomisationAdapter.CommonSortViewHolder>(OffersListDiffCallback()) {

    private var onItemSelectListener: OnItemSelectListener? = null

    class OffersListDiffCallback : DiffUtil.ItemCallback<CustomizationProductDetail.Data>() {

        override fun areItemsTheSame(oldItem: CustomizationProductDetail.Data, newItem: CustomizationProductDetail.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CustomizationProductDetail.Data, newItem: CustomizationProductDetail.Data): Boolean {
            return oldItem.Id == newItem.Id
        }
    }

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    override fun onBindViewHolder(holder: CommonSortViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonSortViewHolder {
        return CommonSortViewHolder(CustomisationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CommonSortViewHolder(private val mBinding: CustomisationListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: CustomizationProductDetail.Data, position: Int) {
            mBinding.apply {
                customSize = item
                priceTextView.paintFlags = priceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                executePendingBindings()

                itemView.setOnClickListener {
                    if (onItemSelectListener != null) {
                        onItemSelectListener?.onItemSelect(item, position)
                    }
                }
            }
        }
    }

    interface OnItemSelectListener {
        fun onItemSelect(item: CustomizationProductDetail.Data, position: Int)
    }

}