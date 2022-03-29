package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductListingResponse(

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
    val hasNextPage: Boolean) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,

        @Expose
        @SerializedName("main_product_id")
        val mainProductId: String,

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
        @SerializedName("rating")
        private val rating: String,

        @Expose
        @SerializedName("discount_value")
        val discountValue: String,

        @Expose
        @SerializedName("name")
        val name: String,

        @Expose
        @SerializedName("images")
        val images: List<Images>,

        @Expose
        @SerializedName("business_category")
        val businessCategory: BusinessCategory,

        @Expose
        @SerializedName("category")
        val category: Category,

        @Expose
        @SerializedName("subcategory")
        val subcategory: Subcategory,

        @Expose
        @SerializedName("price")
        private val price: String,

        @Expose
        @SerializedName("offer_price")
        private val offerPrice: String,

        @Expose
        @SerializedName("is_favourite")
        var isFavourite: Int): Serializable {

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

        data class Images(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("product_image_url")
            val productImageUrl: String,
            @Expose
            @SerializedName("product_image_thumb_url")
            val productImageThumbUrl: String): Serializable

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

        fun getRating(): String {
            if (rating == null) {
                return "0.0"
            } else if (rating.isEmpty()) {
                return "0.0"
            }
            return CommonUtils.convertToDecimalRating(rating)
        }
    }
}