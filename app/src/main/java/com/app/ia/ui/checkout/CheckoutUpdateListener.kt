package com.app.ia.ui.checkout

import com.app.ia.model.CartListResponse

interface CheckoutUpdateListener {
    fun onDeleteItem(productItem: CartListResponse.Docs.CategoryItems, position : Int)
}