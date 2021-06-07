package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetAddressResponse(

    @Expose
    @SerializedName("predictions")
    var predictions: List<Predictions>,
    @Expose
    @SerializedName("status")
    val status: String) {

    data class Predictions(
        @Expose
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("matched_substrings")
        var matchedSubstrings: List<MatchedSubstrings>,
        @Expose
        @SerializedName("place_id")
        val placeId: String,
        @Expose
        @SerializedName("reference")
        val reference: String,
        @Expose
        @SerializedName("structured_formatting")
        var structuredFormatting: StructuredFormatting,
        @Expose
        @SerializedName("terms")
        var terms: List<Terms>,
        @Expose
        @SerializedName("types")
        var types: List<String>)

    data class Terms(
        @Expose
        @SerializedName("offset")
        var offset: Int,
        @Expose
        @SerializedName("value")
        val value: String)

    data class StructuredFormatting(
        @Expose
        @SerializedName("main_text")
        val mainText: String,
        @Expose
        @SerializedName("main_text_matched_substrings")
        var mainTextMatchedSubstrings: List<MainTextMatchedSubstrings>,
        @Expose
        @SerializedName("secondary_text")
        val secondaryText: String)

    data class MainTextMatchedSubstrings(
        @Expose
        @SerializedName("length")
        var length: Int,
        @Expose
        @SerializedName("offset")
        var offset: Int)

    data class MatchedSubstrings(
        @Expose
        @SerializedName("length")
        var length: Int,
        @Expose
        @SerializedName("offset")
        var offset: Int)
}