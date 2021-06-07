package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
        var isActive: Int)

    data class Product(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("inventory_name")
        val inventoryName: String)

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
}
