package com.app.ia.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by umeshk on 11/07/19.
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}