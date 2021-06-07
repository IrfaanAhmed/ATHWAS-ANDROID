package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class RedeemPointResponse(

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
    @SerializedName("totalPages")
    var totalPages: Int,
    @Expose
    @SerializedName("page")
    var page: Int,
    @Expose
    @SerializedName("pagingCounter")
    var pagingcounter: Int,
    @Expose
    @SerializedName("hasPrevPage")
    var hasprevpage: Boolean,
    @Expose
    @SerializedName("hasNextPage")
    var hasnextpage: Boolean,
    @Expose
    @SerializedName("earnedPoints")
    var earnedpoints: String) {

    data class Docs(
        @Expose
        @SerializedName("_id")
        val Id: String,
        @Expose
        @SerializedName("earned_points")
        var earnedPoints: String,
        @Expose
        @SerializedName("redeem_points")
        var redeemPoints: String,
        @Expose
        @SerializedName("user_id")
        val userId: String,
        @Expose
        @SerializedName("expiration_date")
        private val expirationDate: String,
        @Expose
        @SerializedName("createdAt")
        private val createdAt: String,
        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String,
        @Expose
        @SerializedName("__v")
        var V: Int) {

        fun getCreatedAt(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputDate: String
            outputDate = try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(createdAt)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                if(createdAt.isEmpty()) {
                    "01 May 2020, 10:00 AM"
                } else {
                    createdAt
                }
            }
            return outputDate
        }

        fun getExpirationDate(): String {
            val serverDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputDate: String
            outputDate = try {
                val formatter = SimpleDateFormat(serverDateFormat, Locale.ENGLISH)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value: Date = formatter.parse(expirationDate)!!
                val timeZone = TimeZone.getDefault()
                val dateFormatter = SimpleDateFormat("dd MMMM YYYY, h:mm a", Locale.ENGLISH) //this format changeable
                dateFormatter.timeZone = timeZone
                dateFormatter.format(value)
            } catch (e: Exception) {
                if(expirationDate.isEmpty()) {
                    "01 May 2020, 10:00 AM"
                } else {
                    expirationDate
                }
            }
            return outputDate
        }
    }
}
