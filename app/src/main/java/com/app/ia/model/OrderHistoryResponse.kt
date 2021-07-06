package com.app.ia.model

import android.annotation.SuppressLint
import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class OrderHistoryResponse(

    @Expose
    @SerializedName("docs")
    var docs: List<Docs>,
    @Expose
    @SerializedName("totalDocs")
    var totalDocs: Int,
    @Expose
    @SerializedName("limit")
    var limit: Int,
    @Expose
    @SerializedName("totalPages")
    var totalPages: Int,
    @Expose
    @SerializedName("page")
    var page: Int,
    @Expose
    @SerializedName("pagingCounter")
    var pagingcounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    var hasPrevPage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean,
    @Expose
    @SerializedName("nextPage")
    var nextPage: Int) {

    data class Docs(
        @Expose
        @SerializedName("order_status")
        var orderStatus: Int,
        @Expose
        @SerializedName("current_tracking_status")
        var currentTrackingStatus: String,
        @Expose
        @SerializedName("offers")
        var offers: List<String>,
        @Expose
        @SerializedName("payment_mode")
        val paymentMode: String,
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("user_id")
        val userId: String,
        @Expose
        @SerializedName("order_id")
        var orderId: String,
        @Expose
        @SerializedName("total_amount")
        var totalAmount: String,
        @Expose
        @SerializedName("discount")
        var discount: String,
        @Expose
        @SerializedName("net_amount")
        private var netAmount: String,
        @Expose
        @SerializedName("delivery_address")
        val deliveryAddress: String,
        @Expose
        @SerializedName("promo_code")
        val promoCode: String,
        @Expose
        @SerializedName("warehouse_id")
        val warehouseId: String,
        @Expose
        @SerializedName("createdAt")
        private val createdAt: String,
        @Expose
        @SerializedName("expected_start_date")
        private val expectedStartDate: String?,
        @Expose
        @SerializedName("expected_end_date")
        private val expectedEndDate: String?,
        @Expose
        @SerializedName("delivered_date")
        private val deliveredDate: String?,
        @Expose
        @SerializedName("driver_id")
        val driverId: String,
        @Expose
        @SerializedName("id")
        val _id: String) {

        @SuppressLint("NewApi", "WeekBasedYear")
        fun getCreatedAt(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            return try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(createdAt)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                if (createdAt.isEmpty()) {
                    ""
                } else {
                    createdAt
                }
            }
        }

        @SuppressLint("NewApi", "WeekBasedYear")
        private fun getDeliveryDate(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            return try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(expectedEndDate!!)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                if (expectedEndDate.isNullOrEmpty()) {
                    getCreatedAt()
                } else {
                    expectedEndDate
                }
            }
        }

        @SuppressLint("NewApi", "WeekBasedYear")
        fun getDeliveredDate(): String {
            if (deliveredDate == null) {
                return getDeliveryDate()
            } else {
                val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                return try {
                    val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                    val value: Date = formatter.parse(deliveredDate)!!
                    val timeZone = TimeZone.getDefault()
                    val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                    dateFormatter.timeZone = timeZone
                    dateFormatter.format(value)
                } catch (e: Exception) {
                    if (deliveredDate.isNullOrEmpty()) {
                        getDeliveryDate()
                    } else {
                        deliveredDate
                    }
                }
            }
        }

        fun getNetAmount(): String {
            return CommonUtils.convertToDecimal(netAmount)
        }
    }
}
