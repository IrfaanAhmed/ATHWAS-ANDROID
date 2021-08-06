package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CartListResponse(
    @Expose
    @SerializedName("docs")
    var docs: List<Docs>,

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
        @SerializedName("_id")
        var id: Id,
        @Expose
        @SerializedName("category_items")
        var categoryItems: List<CategoryItems>) {

        data class CategoryItems(
            @Expose
            @SerializedName("_id")
            val id: String,
            @Expose
            @SerializedName("cart_id")
            val cartId: String,
            @Expose
            @SerializedName("product_id")
            val productId: String,
            @Expose
            @SerializedName("inventory_id")
            val inventoryId: String,
            @Expose
            @SerializedName("quantity")
            var quantity: Int,
            @Expose
            @SerializedName("available_quantity")
            var availableQuantity: Int,
            @Expose
            @SerializedName("min_inventory")
            var minInventory: Int = 0,
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
            @SerializedName("price")
            private var price: Int,
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
            @SerializedName("description")
            val description: String,
            @Expose
            @SerializedName("offer_price")
            private val offerPrice: String,
            @Expose
            @SerializedName("rating")
            val rating: String,
            @Expose
            @SerializedName("availble")
            var isAvailable: Int) {

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

            fun getRemainingQuantity() : Int {
                return if(availableQuantity < minInventory) {
                    availableQuantity
                } else {
                    minInventory
                }
            }

            fun getPrice() : String {
                return CommonUtils.convertToDecimal(price)
            }

            fun getOfferPrice() : String {
                return CommonUtils.convertToDecimal(offerPrice)
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

        data class Id(
            @Expose
            @SerializedName("_id")
            val Id: String,
            @Expose
            @SerializedName("name")
            val name: String)
    }
}