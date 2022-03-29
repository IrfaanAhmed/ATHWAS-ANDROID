package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class OffersResponse(

    @Expose
    @SerializedName("docs")
    var docs: MutableList<Docs>,
    @Expose
    @SerializedName("totalDocs")
    var totalDocs: Int,
    @Expose
    @SerializedName("limit")
    var limit: Int,
    @Expose
    @SerializedName("page")
    var page: Int,
    @Expose
    @SerializedName("totalPages")
    var totalPages: Int,
    @Expose
    @SerializedName("pagingCounter")
    var pagingCounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    var hasPrevPage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean) {

    data class Docs(

        @Expose
        @SerializedName("image_path_url")
        val imagePathUrl: String,
        @Expose
        @SerializedName("image")
        val bannerImageUrl: String,
        @Expose
        @SerializedName("image_thumb_url")
        val banner_image_thumb_url: String,
        @Expose
        @SerializedName("offer_type")
        val offerType: String,
        @Expose
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("startDate")
        val startDate: String,
        @Expose
        @SerializedName("endDate")
        val endDate: String,
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("coupon_code")
        val couponCode: String,
        @Expose
        @SerializedName("title")
        val title: String,
        @Expose
        @SerializedName("business_category")
        var businessCategory: List<BusinessCategory>,
        @Expose
        @SerializedName("category")
        var category: List<Category>,
        @Expose
        @SerializedName("subcategory")
        var subcategory: List<Subcategory>,
        @Expose
        @SerializedName("product")
        var product: List<Product>,
        @Expose
        @SerializedName("product_id")
        var productId: List<String>,
        @Expose
        @SerializedName("is_active")
        var isActive: Int,) : Serializable{
            fun endTimeInMills(): Long {
                return try {
                    SimpleDateFormat("yyyy-MM-dd", Locale("en")).parse(endDate)!!.time
                } catch (e: Exception){
                    e.printStackTrace()
                    Date().time
                }
            }
        }


    data class Product(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("product_id")
        val productId: String,
        @Expose
        @SerializedName("inventory_name")
        val inventoryName: String,

        @Expose
        @SerializedName("available_quantity")
        val availableQuantity: Int,

        @Expose
        @SerializedName("is_discount")
        val isDiscount: Int,

        @Expose
        @SerializedName("discount_type")
        val discountType: Int,

        @Expose
        @SerializedName("discount_value")
        val discountValue: String,

        @Expose
        @SerializedName("price")
        private val price: String,

        @Expose
        @SerializedName("offer_price")
        private val offerPrice: String,

        @Expose
        @SerializedName("productData")
        var productData: List<ProductListingResponse.Docs>): Serializable{
            fun getDiscountPercent(): String {
                if (isDiscount == 1) {
                    return if (discountType == 1) {
                        "$discountValue% Off"
                    } else {
                        "₹ $discountValue Off"
                    }
                }
                return ""
            }

            fun getPrice(): String {
                return CommonUtils.convertToDecimal(price)
            }

            fun getOfferPrice(): String {
                return CommonUtils.convertToDecimal(offerPrice)
            }

        }

    data class Subcategory(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String): Serializable

    data class Category(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String): Serializable

    data class BusinessCategory(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String): Serializable
}
