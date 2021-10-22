package com.app.ia.ui.notification.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.NotificationListItemBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.NotificationResponse
import com.app.ia.ui.notification.NotificationActivity
import com.app.ia.ui.notification.NotificationViewModel
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.notification_list_item.view.*

class NotificationAdapter(private val mViewModel: NotificationViewModel?) : ListAdapter<NotificationResponse.Docs, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    private val binderHelper = ViewBinderHelper()

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationResponse.Docs>() {

        override fun areItemsTheSame(oldItem: NotificationResponse.Docs, newItem: NotificationResponse.Docs): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(oldItem: NotificationResponse.Docs, newItem: NotificationResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.apply {
            binderHelper.bind(itemView.findViewById(R.id.swipe_layout), getItem(position).Id)
            onBind(getItem(position), position)
            holder.itemView.swipe_layout.dragEdge = SwipeRevealLayout.DRAG_EDGE_RIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(NotificationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class NotificationViewHolder(private val mBinding: NotificationListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(notificationData: NotificationResponse.Docs, position: Int) {
            mBinding.apply {
                notificationItem = notificationData
                executePendingBindings()

                 deleteLayout.setOnClickListener {
                     val tivoDialog = IADialog(itemView.context as Activity, "", "Are you sure you want to delete notification?", "Yes", "No", false)
                     tivoDialog.setOnItemClickListener(object  : IADialog.OnClickListener {

                         override fun onPositiveClick() {
                             mViewModel?.setupObservers(notificationData.Id, null, NotificationActivity.DELETE_NOTIFICATION, position)
                         }

                         override fun onNegativeClick() {

                         }
                     })
                 }
            }
        }
    }
}