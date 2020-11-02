package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductDetailResponse(

    @Expose
    @SerializedName("product")
    var product: Product) {

    data class Product(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("main_product_id")
        val mainProductId: String,
        //inventory_name
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
        @SerializedName("customizations")
        var customizations: List<Customizations>,
        @Expose
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("is_favourite")
        var isFavourite: Int,
        @Expose
        @SerializedName("brand")
        val brand: Brand,
        @Expose
        @SerializedName("offer_price")
        val offerPrice: String,
        @Expose
        @SerializedName("rating")
        val rating: String) {

        data class Customizations(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("title")
            var title: Title)

        data class Title(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String,
            @Expose
            @SerializedName("value")
            var value: Value)

        data class Value(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

        data class Brand(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)

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