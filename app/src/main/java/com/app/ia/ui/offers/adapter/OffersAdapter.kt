package com.app.ia.ui.offers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.R
import com.app.ia.databinding.OfferItemBinding
import com.app.ia.ui.offers.OffersListActivity
import com.app.ia.utils.startActivity

class OffersAdapter(private val product_id: String) : ListAdapter<String, OffersAdapter.OfferViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        return OfferViewHolder(OfferItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OfferViewHolder(private val mBinding: OfferItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(offerName: String, position: Int) {
            mBinding.apply {

                txtView.text = offerName

                when (position) {
                    0 -> {
                        imgView.setImageResource(R.drawable.offer1)
                    }

                    1 -> {
                        imgView.setImageResource(R.drawable.offer2)
                    }

                    2 -> {
                        imgView.setImageResource(R.drawable.offer3)
                    }

                    3 -> {
                        imgView.setImageResource(R.drawable.offer4)
                    }
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity<OffersListActivity> {
                        putExtra("offerName", offerName)
                        putExtra("offerType", (position + 1))
                        putExtra("product_id", product_id)
                    }
                }
                executePendingBindings()
            }
        }
    }
}