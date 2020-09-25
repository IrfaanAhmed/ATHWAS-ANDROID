package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.CommonSortListItemBinding
import com.app.ia.databinding.ProductCategoryListItemBinding
import com.app.ia.model.CommonSortBean
import com.app.ia.ui.product_category.ProductCategoryActivity
import com.app.ia.ui.sub_category.SubCategoryActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.startActivity

class CommonSortAdapter :
    ListAdapter<CommonSortBean, CommonSortAdapter.CommonSortViewHolder>(
        OffersListDiffCallback()
    ) {

    private var onItemSelectListener: OnItemSelectListener? = null

    class OffersListDiffCallback : DiffUtil.ItemCallback<CommonSortBean>() {

        override fun areItemsTheSame(
            oldItem: CommonSortBean,
            newItem: CommonSortBean
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CommonSortBean,
            newItem: CommonSortBean
        ): Boolean {
            return oldItem.equals(newItem)
        }
    }

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    override fun onBindViewHolder(holder: CommonSortViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonSortViewHolder {
        return CommonSortViewHolder(
            CommonSortListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class CommonSortViewHolder(private val mBinding: CommonSortListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(sortOptionItem: CommonSortBean) {
            mBinding.apply {
                sortOption = sortOptionItem
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