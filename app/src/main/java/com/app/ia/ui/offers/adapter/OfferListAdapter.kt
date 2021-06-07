package com.app.ia.ui.offers.adapter

import android.R.attr.label
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OfferListItemBinding
import com.app.ia.model.OffersResponse
import com.app.ia.utils.toast
import kotlinx.android.synthetic.main.offer_list_item.view.*


class OfferListAdapter : ListAdapter<OffersResponse.Docs, OfferListAdapter.OfferViewHolder>(OffersListDiffCallback()) {

    private var onItemSelectListener: OnItemSelectListener? = null

    class OffersListDiffCallback : DiffUtil.ItemCallback<OffersResponse.Docs>() {

        override fun areItemsTheSame(oldItem: OffersResponse.Docs, newItem: OffersResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OffersResponse.Docs, newItem: OffersResponse.Docs): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        return OfferViewHolder(OfferListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OfferViewHolder(private val mBinding: OfferListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(offer: OffersResponse.Docs, position: Int) {
            mBinding.apply {
                promoCodes = offer
                buttonApplyCode.setOnClickListener {
                    onItemSelectListener!!.onItemSelect(offer)
                }

                itemView.setOnClickListener {
                    onItemSelectListener!!.onItemSelect(offer)
                }

                btnCouponCode.setOnClickListener {

                    val clipBoard: ClipboardManager = (itemView.context as Activity).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(offer.couponCode, offer.couponCode)
                    clipBoard.setPrimaryClip(clip)
                    (itemView.context as Activity).toast("Offer code copied!!")
                }
                executePendingBindings()
            }
        }
    }

    interface OnItemSelectListener {
        fun onItemSelect(data: OffersResponse.Docs)
    }
}