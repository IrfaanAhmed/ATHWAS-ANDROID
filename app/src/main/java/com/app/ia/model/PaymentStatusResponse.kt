package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentStatusResponse(

    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("wallet_amount")
    val walletAmount: String,
    @Expose
    @SerializedName("_id")
    val Id: String,
    @Expose
    @SerializedName("user_id")
    val userId: String,
    @Expose
    @SerializedName("order_id")
    val orderId: String,
    @Expose
    @SerializedName("user_type")
    var userType: Int,
    @Expose
    @SerializedName("transition_id")
    val transitionId: String,
    @Expose
    @SerializedName("reason")
    val reason: String,
    @Expose
    @SerializedName("payment_type")
    val paymentType: String,
    @Expose
    @SerializedName("amount_type")
    var amountType: Int,
    @Expose
    @SerializedName("request_type")
    var requestType: Int,
    @Expose
    @SerializedName("payment_transaction_id")
    val paymentTransactionId: String,
    @Expose
    @SerializedName("createdAt")
    val createdAt: String,
    @Expose
    @SerializedName("updatedAt")
    val updatedAt: String,
    @Expose
    @SerializedName("__v")
    var V: Int,
    @Expose
    @SerializedName("id")
    val _id: String) : Serializable
