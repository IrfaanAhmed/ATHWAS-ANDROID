package com.app.ia.ui.faq.adapter

import android.view.LayoutInflater
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ia.databinding.ListItemFaqBinding
import com.app.ia.model.FaqResponse

class FaqAdapter : ListAdapter<FaqResponse.ContentData, FaqAdapter.FaqViewHolder>(OffersListDiffCallback()) {

    class OffersListDiffCallback : DiffUtil.ItemCallback<FaqResponse.ContentData>() {

        override fun areItemsTheSame(oldItem: FaqResponse.ContentData, newItem: FaqResponse.ContentData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FaqResponse.ContentData, newItem: FaqResponse.ContentData): Boolean {
            return oldItem.questionId == newItem.questionId
        }
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.apply {
            onBind(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        return FaqViewHolder(ListItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class FaqViewHolder(private val mBinding: ListItemFaqBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun onBind(productItem: FaqResponse.ContentData, position: Int) {
            mBinding.apply {
                listItem = productItem
                faqPosition = position + 1

                txtViewAnswer.post {
                    productItem.viewHeight = txtViewAnswer.height
                }

                llQuestion.setOnClickListener {
                    if(productItem.isCollapse) {
                        productItem.isCollapse = false
                        imgViewOpenCollapse.animate().setDuration(200).rotation(0f)
                        com.app.ia.utils.ViewAnimationUtils.expand(txtViewAnswer, productItem.viewHeight)
                    } else {
                        productItem.isCollapse = true
                        imgViewOpenCollapse.animate().setDuration(200).rotation(180f)
                        com.app.ia.utils.ViewAnimationUtils.collapse(txtViewAnswer)
                    }
                }
                executePendingBindings()
            }
        }
    }
}