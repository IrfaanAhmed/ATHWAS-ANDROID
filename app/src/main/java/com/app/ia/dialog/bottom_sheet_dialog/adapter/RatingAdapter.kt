package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.CommonSortListItemBinding
import com.app.ia.databinding.RatingListItemBinding
import com.app.ia.model.CommonSortBean

class RatingAdapter : ListAdapter<CommonSortBean, RatingAdapter.RatingViewHolder>(OffersListDiffCallback()) {

    private var onRatingItemSelectListener: OnRatingItemSelectListener? = null

    class OffersListDiffCallback : DiffUtil.ItemCallback<CommonSortBean>() {

        override fun areItemsTheSame(oldItem: CommonSortBean, newItem: CommonSortBean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CommonSortBean, newItem: CommonSortBean): Boolean {
            return oldItem.sortOption == newItem.sortOption
        }
    }

    fun setOnItemSelectListener(onItemSelectListener: OnRatingItemSelectListener) {
        this.onRatingItemSelectListener = onItemSelectListener
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        return RatingViewHolder(RatingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class RatingViewHolder(private val mBinding: RatingListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(sortOptionItem: CommonSortBean, position: Int) {
            mBinding.apply {
                product = sortOptionItem
                executePendingBindings()

                if (sortOptionItem.isSelected) {
                    itemView.setBackgroundResource(R.drawable.primary_color_fill_gradient)
                    //imgViewStar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white), PorterDuff.Mode.MULTIPLY)
                } else {
                    itemView.setBackgroundResource(R.drawable.edittext_bg)
                    //imgViewStar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.light_grey), PorterDuff.Mode.MULTIPLY)
                }

                itemView.setOnClickListener {
                    if (onRatingItemSelectListener != null) {
                        onRatingItemSelectListener?.onRatingSelect(sortOptionItem.sortOption, position)
                    }
                }
            }
        }
    }

    interface OnRatingItemSelectListener {
        fun onRatingSelect(rating: String, position: Int)
    }

}