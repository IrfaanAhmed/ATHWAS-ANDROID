package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FavoriteListResponse(

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
    @SerializedName("totalPages")
    var totalpages: Int,
    @Expose
    @SerializedName("page")
    var page: Int,
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
        val _Id: String,
        @Expose
        @SerializedName("user_id")
        var userId: UserId,
        @Expose
        @SerializedName("product_id")
        val productId: String,
        @Expose
        @SerializedName("createdAt")
        val createdAt: String,
        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String,
        @Expose
        @SerializedName("__v")
        var V: Int,
        @Expose
        @SerializedName("id")
        val id: String) {

        data class UserId(
            @Expose
            @SerializedName("_id")
            val _Id: String,
            @Expose
            @SerializedName("username")
            val username: String,
            @Expose
            @SerializedName("register_id")
            val registerId: String,
            @Expose
            @SerializedName("user_image_url")
            val userImageUrl: String,
            @Expose
            @SerializedName("user_image_thumb_url")
            val userImageThumbUrl: String,
            @Expose
            @SerializedName("id")
            val id: String)
    }
}