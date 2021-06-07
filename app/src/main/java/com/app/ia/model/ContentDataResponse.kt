package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContentDataResponse(
    @Expose
    @SerializedName("content_data")
    val contentData: String)