package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
        val price: String,
        @Expose
        @SerializedName("is_favourite")
        var isFavourite: Int)

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