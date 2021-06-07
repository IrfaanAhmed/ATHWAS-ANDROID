package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CustomizationTypeResponse(

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
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String)
}