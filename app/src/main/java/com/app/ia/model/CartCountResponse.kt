package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CartCountResponse(

    @Expose
    @SerializedName("cart_count")
    var cartCount: Int,
    @Expose
    @SerializedName("notification_count")
    var notificationCount: Int)