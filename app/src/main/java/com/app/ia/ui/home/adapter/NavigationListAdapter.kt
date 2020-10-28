package com.app.ia.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.SideMenuListItemBinding

class NavigationListAdapter : ListAdapter<String, NavigationListAdapter.NavigationViewHolder>(OffersListDiffCallback()) {

    var selectedMenuPosition = 0

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private var onSideMenuItemClickListener: OnSideMenuItemClickListener? = null

    fun setOnSideMenuClickListener(onSideMenuItemClickListener: OnSideMenuItemClickListener) {
        this.onSideMenuItemClickListener = onSideMenuItemClickListener
    }


    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(SideMenuListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class NavigationViewHolder(private val mBinding: SideMenuListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(item: String, position: Int) {
            mBinding.apply {
                navigationItem = item
                executePendingBindings()

                if (position == selectedMenuPosition) {
                    textViewMenu.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
                } else {
                    textViewMenu.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_grey))
                }

                itemView.setOnClickListener {
                    if (onSideMenuItemClickListener != null) {
                        onSideMenuItemClickListener?.onSideMenuClick(position)
                        selectedMenuPosition = position
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }


    interface OnSideMenuItemClickListener {
        fun onSideMenuClick(position: Int)
    }
}