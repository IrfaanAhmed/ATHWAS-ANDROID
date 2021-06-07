package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoDataResponse(

    val noData: String = "",

    @Expose
    @SerializedName("user_id")
    val userId: String,

    @Expose
    @SerializedName("wallet")
    val wallet: String,

    @Expose
    @SerializedName("cart_count")
    val cartCount: Int,

    @Expose
    @SerializedName("path")
    val path: String,

    @Expose
    @SerializedName("user_role")
    val userRole: String)