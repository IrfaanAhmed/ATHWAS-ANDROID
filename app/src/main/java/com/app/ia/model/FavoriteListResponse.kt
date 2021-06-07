package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FavoriteListResponse(

    @Expose
    @SerializedName("docs")
    var docs: MutableList<Docs>,
    @Expose
    @SerializedName("totalDocs")
    var totaldocs: Int,
    @Expose
    @SerializedName("limit")
    var limit: Int,
    @Expose
    @SerializedName("page")
    var page: Int,
    @Expose
    @SerializedName("totalPages")
    var totalpages: Int,
    @Expose
    @SerializedName("pagingCounter")
    var pagingCounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    var hasprevpage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    var hasnextpage: Boolean) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("inventory_name")
        val inventoryName: String,
        @Expose
        @SerializedName("inventory_id")
        val inventoryId: String,
        @Expose
        @SerializedName("product_id")
        val productId: String,
        @Expose
        @SerializedName("price")
        var price: String,
        @Expose
        @SerializedName("available_quantity")
        var quantity: Int,
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
        @SerializedName("product_code")
        val productCode: String,
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
        @SerializedName("offer_price")
        val offerPrice: String) {

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
    }
}