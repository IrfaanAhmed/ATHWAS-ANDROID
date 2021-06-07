package com.app.ia.ui.my_cart

import com.app.ia.model.CartListResponse

interface CartUpdateListener {

    fun onUpdate(productItem: CartListResponse.Docs.CategoryItems, position : Int)
    fun onDeleteItem(productItem: CartListResponse.Docs.CategoryItems, position : Int)
}