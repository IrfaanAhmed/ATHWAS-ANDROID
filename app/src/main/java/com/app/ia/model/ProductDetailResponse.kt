package com.app.ia.model

import com.app.ia.utils.CommonUtils
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
        private var price: String,

        @Expose
        @SerializedName("discount_type")
        val discountType: Int,

        @Expose
        @SerializedName("discount_value")
        val discountValue: String,

        @Expose
        @SerializedName("quantity")
        var quantity: Int,

        @Expose
        @SerializedName("min_inventory")
        var minInventory: Int = 0,

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
        @SerializedName("is_discount")
        val isDiscount: Int,

        @Expose
        @SerializedName("brand")
        val brand: Brand,

        @Expose
        @SerializedName("offer_price")
        private val offerPrice: String,

        @Expose
        @SerializedName("rating")
        val rating: String,

        @Expose
        @SerializedName("ratingCount")
        val ratingCount: String) {

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