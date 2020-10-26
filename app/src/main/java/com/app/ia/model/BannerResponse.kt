package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerResponse(

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
    var hasnextpage: Boolean) {

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
        @SerializedName("banner_image_url")
        val bannerImageUrl: String,
        @Expose
        @SerializedName("banner_image_thumb_url")
        val bannerImageThumbUrl: String,
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