package com.app.ia.ui.order_detail.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.OrderDetailListItemBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.OrderDetailResponse
import com.app.ia.ui.order_detail.OrderDetailActivity
import com.app.ia.ui.product_detail.ProductDetailActivity
import com.app.ia.ui.write_review.WriteReviewActivity
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.order_detail_list_item.view.*

class OrderDetailListItemAdapter(private val allReturn: Boolean, private val cancelAvailable: Boolean, private val returnAvailable: Boolean, private val mainOrderStatus: Int) : ListAdapter<OrderDetailResponse.Category.Products, OrderDetailListItemAdapter.OrderDetailViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<OrderDetailResponse.Category.Products>() {

        override fun areItemsTheSame(oldItem: OrderDetailResponse.Category.Products, newItem: OrderDetailResponse.Category.Products): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderDetailResponse.Category.Products, newItem: OrderDetailResponse.Category.Products): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder(OrderDetailListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class OrderDetailViewHolder(private val mBinding: OrderDetailListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: OrderDetailResponse.Category.Products, position: Int) {
            mBinding.apply {
                product = productItem
                isAllReturn = allReturn
                isCancelAvailable = cancelAvailable
                isReturnAvailable = returnAvailable
                isItemCancelled = (!allReturn && productItem.orderStatus == 4)
                isItemReturned = (!allReturn && (productItem.orderStatus == 2 || productItem.orderStatus == 1 || productItem.orderStatus == 3))
                itemView.tvActualPrice.paintFlags = itemView.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                btnReturn.setOnClickListener {

                    val returnDialog = IADialog(itemView.context as Activity, "Do you want to return this product?", false)
                    returnDialog.setOnItemClickListener(object : IADialog.OnClickListener {
                        override fun onPositiveClick() {
                            if (onItemSelectListener != null) {
                                onItemSelectListener?.onProductReturn(productItem, position)
                            }
                        }

                        override fun onNegativeClick() {

                        }
                    })

                }

                btnCancel.setOnClickListener {
                    val returnDialog = IADialog(itemView.context as Activity, "Do you want to cancel this product?", false)
                    returnDialog.setOnItemClickListener(object : IADialog.OnClickListener {
                        override fun onPositiveClick() {
                            if (onItemSelectListener != null) {
                                onItemSelectListener?.onProductCancel(productItem, position)
                            }
                        }

                        override fun onNegativeClick() {

                        }
                    })
                }

                if (mainOrderStatus == 2) {
                    txtWriteReview.visibility = View.VISIBLE
                } else {
                    txtWriteReview.visibility = View.INVISIBLE
                }

                txtWriteReview.setOnClickListener {
                    itemView.context.startActivity<WriteReviewActivity> {
                        putExtra("productDetail", productItem)
                        putExtra("order_id", (itemView.context as OrderDetailActivity).mViewModel!!.order_id.value)
                    }
                }

                materialCardView.setOnClickListener {
                    itemView.context.startActivity<ProductDetailActivity> {
                        putExtra("product_id", productItem.inventoryId)
                    }
                }
                executePendingBindings()
            }
        }
    }

    private var onItemSelectListener: OnItemSelectListener? = null

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    interface OnItemSelectListener {
        fun onProductReturn(returnProduct: OrderDetailResponse.Category.Products, position: Int)
        fun onProductCancel(cancelProduct: OrderDetailResponse.Category.Products, position: Int)

    }
}