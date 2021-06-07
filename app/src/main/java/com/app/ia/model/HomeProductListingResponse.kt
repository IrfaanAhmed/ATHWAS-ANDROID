package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HomeProductListingResponse(

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
    var hasNextPage: Boolean,
    @Expose
    @SerializedName("nextPage")
    var nextPage: Int) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("main_product_id")
        val mainProductId: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("inventory_name")
        val inventoryName: String,
        @Expose
        @SerializedName("images")
        var images: List<Images>,
        @Expose
        @SerializedName("business_category")
        var businessCategory: BusinessCategory,
        @Expose
        @SerializedName("category")
        var category: Category,
        @Expose
        @SerializedName("subcategory")
        var subcategory: Subcategory,
        @Expose
        @SerializedName("available_quantity")
        var availableQuantity: Int,
        @Expose
        @SerializedName("price")
        private var price: String,
        @Expose
        @SerializedName("is_favourite")
        var isFavourite: Int,
        @Expose
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("discount_type")
        var discountType: Int,
        @Expose
        @SerializedName("discount_value")
        var discountValue: String,
        @Expose
        @SerializedName("is_discount")
        var isDiscount: Int,
        @Expose
        @SerializedName("offer_price")
        private var offerPrice: String,
        @Expose
        @SerializedName("rating")
        val rating: String) {

        data class Subcategory(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

        data class Category(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

        data class BusinessCategory(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

        data class Images(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("product_image_url")
            val productImageUrl: String,
            @Expose
            @SerializedName("product_image_thumb_url")
            val productImageThumbUrl: String)

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

        fun getPrice() : String {
            return CommonUtils.convertToDecimal(price)
        }

        fun getOfferPrice() : String {
            return CommonUtils.convertToDecimal(offerPrice)
        }
    }
}