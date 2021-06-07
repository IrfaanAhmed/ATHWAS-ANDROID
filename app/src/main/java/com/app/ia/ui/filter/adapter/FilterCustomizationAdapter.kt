package com.app.ia.ui.filter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.FilterCustomizationListItemBinding
import com.app.ia.model.CustomizationTypeResponse
import com.app.ia.utils.getColorCompat

class FilterCustomizationAdapter : ListAdapter<CustomizationTypeResponse.Docs, FilterCustomizationAdapter.CustomizationTypeViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<CustomizationTypeResponse.Docs>() {

        override fun areItemsTheSame(oldItem: CustomizationTypeResponse.Docs, newItem: CustomizationTypeResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CustomizationTypeResponse.Docs, newItem: CustomizationTypeResponse.Docs): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onBindViewHolder(holder: CustomizationTypeViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomizationTypeViewHolder {
        return CustomizationTypeViewHolder(FilterCustomizationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CustomizationTypeViewHolder(private val mBinding: FilterCustomizationListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(customizationName: CustomizationTypeResponse.Docs, position: Int) {

            mBinding.apply {
                name = customizationName

                if (position == selectedPosition) {
                    itemView.setBackgroundColor((itemView.context as Activity).getColorCompat(R.color.white))
                    txtCustomizationName.setTextColor((itemView.context as Activity).getColorCompat(R.color.blue))
                } else {
                    itemView.setBackgroundColor((itemView.context as Activity).getColorCompat(R.color.transparent))
                    txtCustomizationName.setTextColor((itemView.context as Activity).getColorCompat(R.color.black))
                }

                itemView.setOnClickListener {
                    selectedPosition = position
                    notifyDataSetChanged()

                    if (onCustomizationTypeClickListener != null) {
                        onCustomizationTypeClickListener?.onSimilarProductClick(customizationName, position)
                    }
                }
                executePendingBindings()
            }
        }
    }

    var selectedPosition = 0

    private var onCustomizationTypeClickListener: OnCustomizationTypeClickListener? = null

    fun setOnSimilarProductClickListener(onCustomizationTypeClickListener: OnCustomizationTypeClickListener) {
        this.onCustomizationTypeClickListener = onCustomizationTypeClickListener
    }

    interface OnCustomizationTypeClickListener {
        fun onSimilarProductClick(customizationType: CustomizationTypeResponse.Docs, position: Int)
    }
}