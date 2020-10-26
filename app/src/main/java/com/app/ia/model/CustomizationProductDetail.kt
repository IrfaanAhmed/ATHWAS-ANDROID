package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.StringBuilder

data class CustomizationProductDetail(

    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("api_name")
    val apiName: String,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("data")
    var data: List<Data>) {

    data class Data(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("customize")
        var customize: List<Customize>,
        @Expose
        @SerializedName("images")
        val images: String,
        @Expose
        @SerializedName("price")
        var price: Int,
        @Expose
        @SerializedName("offer_price")
        val offerPrice: String,
        @Expose
        @SerializedName("rating")
        val rating: String,

        var isSelected : Boolean = false) {

        fun getCustomizeValue(): String {
            var finalValue = ""
            if (customize.isNotEmpty()) {
                val stringBuilder = StringBuilder()
                for (value in customize) {
                    stringBuilder.append(value.value.name).append(" - ")
                }
                finalValue = stringBuilder.toString().subSequence(0, stringBuilder.toString().length - 2).toString()
            }
            return finalValue
        }
    }

    data class Customize(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("value")
        var value: Value)

    data class Value(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("name")
        val name: String)
}