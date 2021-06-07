package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class WalletHistoryResponse(

    @Expose
    @SerializedName("docs")
    val docs: MutableList<Docs>,
    @Expose
    @SerializedName("totalDocs")
    val totalDocs: Int,
    @Expose
    @SerializedName("limit")
    val limit: Int,
    @Expose
    @SerializedName("page")
    val page: Int,
    @Expose
    @SerializedName("totalPages")
    val totalPages: Int,
    @Expose
    @SerializedName("pagingCounter")
    val pagingCounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    val hasPrevPage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean,
    @Expose
    @SerializedName("user_wallet")
    val userWallet: Double) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("amount")
        private val amount: String,
        @Expose
        @SerializedName("wallet_amount")
        val walletAmount: String,
        @Expose
        @SerializedName("user_id")
        val userId: String,
        @Expose
        @SerializedName("transition_id")
        val transitionId: String,
        @Expose
        @SerializedName("reason")
        val reason: String,
        @Expose
        @SerializedName("sender_id")
        val senderId: String,
        @Expose
        @SerializedName("user_type")
        val userType: Int,
        @Expose
        @SerializedName("sender_type")
        val senderType: Int,
        @Expose
        @SerializedName("payment_type")
        val paymentType: String,
        @Expose
        @SerializedName("amount_type")
        val amountType: Int,
        @Expose
        @SerializedName("request_type")
        val requestType: Int,
        @Expose
        @SerializedName("order_id")
        val orderId: String,
        @Expose
        @SerializedName("createdAt")
        private val createdAt: String,
        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String,
        @Expose
        @SerializedName("__v")
        val V: Int) {

        fun getCreatedAt(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputDate: String
            outputDate = try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(createdAt)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                createdAt
            }
            return outputDate
        }

        fun getAmount() : String {
           return CommonUtils.convertToDecimal(amount)
        }
    }
}