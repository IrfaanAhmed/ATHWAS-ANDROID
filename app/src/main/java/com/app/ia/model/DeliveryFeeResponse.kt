package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeliveryFeeResponse(

    @Expose
    @SerializedName("delivery_fee")
    val deliveryFee: String,

    @Expose
    @SerializedName("warehouse")
    val warehouse: String,

    @Expose
    @SerializedName("wallet")
    val wallet: String,

    @Expose
    @SerializedName("vat")
    val vat: String,

    @Expose
    @SerializedName("vat_amount")
    val vatAmount: String)