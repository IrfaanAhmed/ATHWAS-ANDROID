package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BusinessCategoryResponse(

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
    @SerializedName("totalPages")
    val totalPages: Int,
    @Expose
    @SerializedName("page")
    val page: Int,
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
        val _Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("createdAt")
        val createdAt: String,
        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String,
        @Expose
        @SerializedName("category_image")
        val categoryImage: String,
        @Expose
        @SerializedName("category_image_url")
        val categoryImageUrl: String,
        @Expose
        @SerializedName("category_image_thumb_url")
        val categoryImageThumbUrl: String,
        @Expose
        @SerializedName("id")
        val id: String)
}