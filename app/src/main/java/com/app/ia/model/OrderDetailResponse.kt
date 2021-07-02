package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.app.ia.utils.redact
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


data class OrderDetailResponse(

    @Expose
    @SerializedName("_id")
    val Id: String,
    @Expose
    @SerializedName("category")
    var category: List<Category>,
    @Expose
    @SerializedName("current_tracking_status")
    val currentTrackingStatus: String,
    @Expose
    @SerializedName("order_id")
    var orderId: String,
    @Expose
    @SerializedName("redeem_points")
    private var redeemPoints: String?,
    @Expose
    @SerializedName("userData")
    var userdata: Userdata,
    @Expose
    @SerializedName("warehouseData")
    var warehousedata: List<WareHouseData>,
    @Expose
    @SerializedName("driver")
    var driver: List<Driver>,
    @Expose
    @SerializedName("promo_code")
    var promoCode: String,
    @Expose
    @SerializedName("order_status")
    var orderStatus: Int,
    @Expose
    @SerializedName("tracking_status")
    var trackingStatus: TrackingStatus,
    @Expose
    @SerializedName("offers")
    var offers: List<String>,
    @Expose
    @SerializedName("payment_mode")
    val paymentMode: String,
    @Expose
    @SerializedName("total_amount")
    private var totalAmount: String,
    @Expose
    @SerializedName("discount")
    private var discount: String,
    @Expose
    @SerializedName("net_amount")
    private var netAmount: String,
    @Expose
    @SerializedName("vat_amount")
    private var vat_amount: String,
    @Expose
    @SerializedName("delivery_fee")
    private var deliveryFee: String,
    @Expose
    @SerializedName("delivery_address")
    var deliveryAddress: DeliveryAddress,
    @Expose
    @SerializedName("order_date")
    val orderDate: String,
    @Expose
    @SerializedName("expected_start_date")
    private val expectedStartDate: String?,
    @Expose
    @SerializedName("expected_end_date")
    private val expectedEndDate: String?,
    @Expose
    @SerializedName("delivered_date")
    val deliveredDate: String?,
    @Expose
    @SerializedName("order_cancel_time")
    val orderCancelTime: String,
    @Expose
    @SerializedName("order_return_time")
    val orderReturnTime: String) {

    constructor() : this("", ArrayList(), "", "", "", Userdata(), ArrayList<WareHouseData>(), ArrayList<Driver>(), "", 1, TrackingStatus(), ArrayList(), "", "0", "0", "0", "0", "0", DeliveryAddress(), "", "", "", "", "", "")

    fun getOrderDate1(): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: String
        outputDate = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(orderDate)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            getOrderStartDate()
        }
        return outputDate
    }

    fun getOrderStartDate(): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: String = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(expectedStartDate!!)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            if (expectedStartDate.isNullOrEmpty()) {
                ""//getOrderDate1()
            } else {
                expectedStartDate
            }
        }
        return outputDate
    }

    private fun getDeliveryDate(): String {
        val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputDate: String = try {
            val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value: Date = formatter.parse(expectedEndDate!!)!!
            val timeZone = TimeZone.getDefault()
            val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
            dateFormatter.timeZone = timeZone
            dateFormatter.format(value)
        } catch (e: Exception) {
            if (expectedEndDate.isNullOrEmpty()) {
                ""//getOrderDate1()
            } else {
                expectedEndDate
            }
        }
        return outputDate
    }


    fun getFinalDeliveryDate(): String {
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

    fun getRedeemPoints() : String {
        return if(redeemPoints.isNullOrEmpty()) {
            "0"
        } else {
            if(redeemPoints!!.toDouble() > 0) {
                redeemPoints!!
            } else {
                "0"
            }
        }
    }

    fun getNetAmount(): String {
        return CommonUtils.convertToDecimal(netAmount)
    }

    fun getTotalAmount(): String {
        if (totalAmount == null) {
            totalAmount = "0"
        }
        return CommonUtils.convertToDecimal(totalAmount)
    }

    fun getVatAmount(): String {
        return CommonUtils.convertToDecimal(vat_amount)
    }

    fun getDiscount(): String {
        if (discount == null) {
            discount = "0"
        }
        return CommonUtils.convertToDecimal(discount)
    }

    fun getDeliveryFee(): String {
        return CommonUtils.convertToDecimal(deliveryFee)
    }

    data class DeliveryAddress(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("full_address")
        val fullAddress: String,
        @Expose
        @SerializedName("address_type")
        val addressType: String,
        @Expose
        @SerializedName("address_location")
        var addressLocation: AddressLocation) {

        constructor() : this("", "", "", AddressLocation())

        data class AddressLocation(
            @Expose
            @SerializedName("type")
            val type: String,
            @Expose
            @SerializedName("coordinates")
            var coordinates: List<Double>,
            @Expose
            @SerializedName("_id")
            val Id: String) {

            constructor() : this("", ArrayList(), "")
        }

    }

    data class TrackingStatus(
        @Expose
        @SerializedName("Acknowledged")
        var acknowledged: Acknowledged,
        @Expose
        @SerializedName("Packed")
        var packed: Packed,
        @Expose
        @SerializedName("In_Transit")
        var inTransit: InTransit,
        @Expose
        @SerializedName("Delivered")
        var delivered: Delivered) {
        constructor() : this(Acknowledged(), Packed(), InTransit(), Delivered())
    }

    data class Delivered(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String) {
        constructor() : this(0, "", "")
    }

    data class InTransit(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String) {
        constructor() : this(0, "", "")
    }

    data class Packed(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String) {
        constructor() : this(0, "", "")
    }

    data class Acknowledged(
        @Expose
        @SerializedName("status")
        var status: Int,
        @Expose
        @SerializedName("status_title")
        val statusTitle: String,
        @Expose
        @SerializedName("time")
        val time: String) {
        constructor() : this(1, "", "")
    }

    data class WareHouseData(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("address")
        val address: String) {

        constructor() : this("", "", "")
    }

    data class Userdata(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("username")
        val username: String,
        @Expose
        @SerializedName("phone")
        val phone: String) {

        constructor() : this("", "", "")
    }

    data class Driver(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("username")
        val username: String,
        @Expose
        @SerializedName("phone")
        val phone: String) {

        constructor() : this("", "", "")

        fun getMaskedPhoneNumber(): String {
            var number = phone
            if (phone.isNotEmpty()) {
                number = phone.redact()
            }
            return number
        }
    }

    data class Category(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("cancelation_time")
        var cancellationTime: Int,
        @Expose
        @SerializedName("return_time")
        var returnTime: Int,
        @Expose
        @SerializedName("all_return")
        var allReturn: Int,
        @Expose
        @SerializedName("category_image")
        val categoryImage: String,
        @Expose
        @SerializedName("products")
        var products: List<Products>) {

        data class Products(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("images")
            var images: List<Images>,
            @Expose
            @SerializedName("is_active")
            var isActive: Int,
            @Expose
            @SerializedName("is_deleted")
            var isDeleted: Int,
            @Expose
            @SerializedName("name")
            val name: String,
            @Expose
            @SerializedName("business_category_id")
            val businessCategoryId: String,
            @Expose
            @SerializedName("brand_id")
            val brandId: String,
            @Expose
            @SerializedName("inventory_id")
            val inventoryId: String,
            @Expose
            @SerializedName("category_id")
            val categoryId: String,
            @Expose
            @SerializedName("sub_category_id")
            val subCategoryId: String,
            @Expose
            @SerializedName("description")
            val description: String,
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
            @SerializedName("quantity")
            var quantity: Int,
            @Expose
            @SerializedName("price")
            private var price: String,
            @Expose
            @SerializedName("offer_price")
            private val offerPrice: String,
            @Expose
            @SerializedName("is_discount")
            val isDiscount: Int,
            @Expose
            @SerializedName("order_status")
            var orderStatus: Int) : Serializable {

            fun getPrice(): String {
                return CommonUtils.convertToDecimal(price)
            }

            fun getOfferPrice(): String {
                return CommonUtils.convertToDecimal(offerPrice)
            }

            data class Images(
                @Expose
                @SerializedName("_id")
                val Id: String,
                @Expose
                @SerializedName("product_image_url")
                val productImageUrl: String,
                @Expose
                @SerializedName("product_image_thumb_url")
                val productImageThumbUrl: String) : Serializable
        }
    }
}
