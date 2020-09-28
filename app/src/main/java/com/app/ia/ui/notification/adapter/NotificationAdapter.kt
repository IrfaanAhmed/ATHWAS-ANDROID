package com.app.wallet.tivo.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.NotificationListItemBinding
import com.app.ia.model.NotificationResponse
import com.app.ia.ui.notification.NotificationViewModel

class NotificationAdapter : ListAdapter<NotificationResponse.Docs, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {


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
            onBind(getItem(position), position)
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

                /* deleteLayout.setOnClickListener {
                     val tivoDialog = TivoDialog(itemView.context as Activity, "Are you sure you want to delete notification?", false)
                     tivoDialog.setOnItemClickListener(object  : TivoDialog.OnClickListener {

                         override fun onPositiveClick() {
                             mViewModel?.setupObservers(notificationData.Id, null, NotificationActivity.DELETE_NOTIFICATION, position)
                         }

                         override fun onNegativeClick() {

                         }
                     })
                 }*/
            }
        }
    }
}