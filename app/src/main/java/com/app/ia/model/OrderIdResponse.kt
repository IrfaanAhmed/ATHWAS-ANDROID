package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderIdResponse(

    @Expose
    @SerializedName("_id")
    val Id: String,
    @Expose
    @SerializedName("order_id")
    var orderId: String,
    @Expose
    @SerializedName("payment_mode")
    val paymentMode: String)