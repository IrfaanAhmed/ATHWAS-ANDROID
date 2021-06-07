package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DealOfTheDayBannerResponse(

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
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("title")
        val title: String,
        @Expose
        @SerializedName("image_url")
        val imageUrl: String,
        @Expose
        @SerializedName("image_thumb_url")
        val imageThumbUrl: String,
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
        @SerializedName("is_active")
        var isActive: Int)

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