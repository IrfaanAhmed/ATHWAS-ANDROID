package com.app.ia.model

import com.app.ia.utils.CommonUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SimilarProductListResponse(


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
    var pagingcounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    var hasprevpage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    var hasnextpage: Boolean,
    @Expose
    @SerializedName("nextPage")
    var nextpage: Int) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
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
        private var price: String,
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
        @SerializedName("is_discount")
        val isDiscount: Int,
        @Expose
        @SerializedName("discount_type")
        val discountType: Int,
        @Expose
        @SerializedName("discount_value")
        val discountValue: String) {

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