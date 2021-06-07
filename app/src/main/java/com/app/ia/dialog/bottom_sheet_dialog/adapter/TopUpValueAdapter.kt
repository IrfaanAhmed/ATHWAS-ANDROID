package com.app.ia.dialog.bottom_sheet_dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.TopUpValueListItemBinding
import com.app.ia.dialog.bottom_sheet_dialog.AddMoneyDialogFragment

class TopUpValueAdapter(private val topUpDialog: AddMoneyDialogFragment) : ListAdapter<String, TopUpValueAdapter.TopUpValueViewHolder>(OffersListDiffCallback()) {

    var selectedPosition: Int = -1

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: TopUpValueViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUpValueViewHolder {
        return TopUpValueViewHolder(TopUpValueListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class TopUpValueViewHolder(private val mBinding: TopUpValueListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(value: String, position: Int) {
            mBinding.apply {
                topUpValue = value

                if (selectedPosition == position) {
                    txtViewTopUp.setBackgroundResource(R.drawable.primary_color_fill_gradient)
                    txtViewTopUp.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else {
                    txtViewTopUp.setBackgroundResource(R.drawable.edittext_bg)
                    txtViewTopUp.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
                }

                txtViewTopUp.setOnClickListener {
                    topUpDialog.clearAmountValue(value)
                    selectedPosition = position
                    notifyDataSetChanged()
                }

                executePendingBindings()
            }
        }
    }
}