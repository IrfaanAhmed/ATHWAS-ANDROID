package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BrandResponse(

    @Expose
    @SerializedName("docs")
    val docs: List<Docs>,
    @Expose
    @SerializedName("totalDocs")
    val totalDocs: Int,
    @Expose
    @SerializedName("limit")
    val limit: Int,
    @Expose
    @SerializedName("totalPages")
    val totalpages: Int,
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
        @SerializedName("is_active")
        val isActive: Int,
        @Expose
        @SerializedName("is_deleted")
        val isDeleted: Int,
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
        @SerializedName("__v")
        val V: Int,
        @Expose
        @SerializedName("image_path_url")
        val imagePathUrl: String,
        @Expose
        @SerializedName("image_path_thumb_url")
        val imagePathThumbUrl: String,
        @Expose
        @SerializedName("id")
        val id: String)
}