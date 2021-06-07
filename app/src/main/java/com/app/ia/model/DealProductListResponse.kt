package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DealProductListResponse(

    @Expose
    @SerializedName("_id")
    val Id: String,

    @Expose
    @SerializedName("productInventoriesData")
    var productInventoriesData: List<ProductListingResponse.Docs>) {

    data class ProductInventoriesData(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("main_product_id")
        val mainProductId: String,
        @Expose
        @SerializedName("inventory_name")
        val inventoryName: String,
        @Expose
        @SerializedName("price")
        var price: Int,
        @Expose
        @SerializedName("quantity")
        var quantity: Int,
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
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("is_favourite")
        var isFavourite: Int,
        @Expose
        @SerializedName("is_discount")
        var isDiscount: Int,
        @Expose
        @SerializedName("discount_type")
        var discountType: Int,
        @Expose
        @SerializedName("discount_value")
        var discountValue: Int,
        @Expose
        @SerializedName("offer_price")
        var offerPrice: Int)

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
