package com.app.ia.ui.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.HomeProductListItemBinding
import com.app.ia.model.HomeProductListingResponse
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.utils.startActivity

class HomeProductAdapter : ListAdapter<HomeProductListingResponse.Docs, HomeProductAdapter.HomeProductViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<HomeProductListingResponse.Docs>() {

        override fun areItemsTheSame(oldItem: HomeProductListingResponse.Docs, newItem: HomeProductListingResponse.Docs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HomeProductListingResponse.Docs, newItem: HomeProductListingResponse.Docs): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onBindViewHolder(holder: HomeProductViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductViewHolder {
        return HomeProductViewHolder(HomeProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class HomeProductViewHolder(private val mBinding: HomeProductListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(favourite: HomeProductListingResponse.Docs) {

            mBinding.apply {
                product = favourite

                if (favourite.isDiscount == 1) {
                    txtDiscountPrice.paintFlags = txtDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> {
                        putExtra("product_id", favourite.Id)
                    }
                }
                executePendingBindings()
            }
        }
    }
}