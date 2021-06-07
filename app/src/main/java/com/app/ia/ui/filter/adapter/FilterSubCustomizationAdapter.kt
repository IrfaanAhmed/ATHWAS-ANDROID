package com.app.ia.ui.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.FilterSubCustomizationListItemBinding
import com.app.ia.model.CustomizationSubTypeResponse

class FilterSubCustomizationAdapter : ListAdapter<CustomizationSubTypeResponse.CustomizationSubType, FilterSubCustomizationAdapter.CustomizationSubTypeViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<CustomizationSubTypeResponse.CustomizationSubType>() {

        override fun areItemsTheSame(oldItem: CustomizationSubTypeResponse.CustomizationSubType, newItem: CustomizationSubTypeResponse.CustomizationSubType): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CustomizationSubTypeResponse.CustomizationSubType, newItem: CustomizationSubTypeResponse.CustomizationSubType): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onBindViewHolder(holder: CustomizationSubTypeViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomizationSubTypeViewHolder {
        return CustomizationSubTypeViewHolder(FilterSubCustomizationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class CustomizationSubTypeViewHolder(private val mBinding: FilterSubCustomizationListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(customizationName: CustomizationSubTypeResponse.CustomizationSubType, position: Int) {

            mBinding.apply {
                name = customizationName.name
                executePendingBindings()
                checkboxSubType.isChecked = customizationName.isSelected

                checkboxSubType.setOnClickListener {
                    customizationName.isSelected = checkboxSubType.isChecked
                    if(onSubCustomizationClickListener != null) {
                        onSubCustomizationClickListener?.onSubCustomizationClick(customizationName, position)
                    }
                }
            }
        }
    }

    private var onSubCustomizationClickListener: OnSubCustomizationClickListener? = null

    fun setOnSubCustomizationClickListener(onSubCustomizationClickListener: OnSubCustomizationClickListener) {
        this.onSubCustomizationClickListener = onSubCustomizationClickListener
    }

    interface OnSubCustomizationClickListener {
        fun onSubCustomizationClick(customization : CustomizationSubTypeResponse.CustomizationSubType, position: Int)
    }
}