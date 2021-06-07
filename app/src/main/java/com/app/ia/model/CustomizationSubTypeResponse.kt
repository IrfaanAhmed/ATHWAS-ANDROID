package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomizationSubTypeResponse(

    @Expose
    @SerializedName("customizationsub_type")
    var customizationSubType: MutableList<CustomizationSubType> = ArrayList()) {

    data class CustomizationSubType(
        @Expose
        @SerializedName("_id")
        val Id: String,

        @Expose
        @SerializedName("name")
        val name: String,

        var isSelected : Boolean) : Serializable
}
