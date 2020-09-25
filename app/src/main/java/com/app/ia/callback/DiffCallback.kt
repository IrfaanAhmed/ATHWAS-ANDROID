package com.app.ia.callback

import androidx.recyclerview.widget.DiffUtil

class DiffCallback : DiffUtil.ItemCallback<Any>() {

    private var oldList: List<Any> = emptyList()
    private var newList: List<Any> = emptyList()

    fun setList(oldList: List<Any>, newList: List<Any>) {
        this.oldList = oldList
        this.newList = newList
    }

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldList == newList
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldList == newList
    }

}