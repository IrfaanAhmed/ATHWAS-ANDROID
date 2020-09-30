package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class NotificationResponse(

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
    var totalPages: Int,

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
        @SerializedName("user_id_ref")
        val userIdRef: String = "",

        @Expose
        @SerializedName("action_type")
        val actionType: String = "",

        @Expose
        @SerializedName("action_by_ref")
        val actionByRef: String = "",

        @Expose
        @SerializedName("redirection")
        val redirection: String = "",

        @Expose
        @SerializedName("_id")
        val Id: String = "",

        @Expose
        @SerializedName("user_id")
        val userId: String = "",

        @Expose
        @SerializedName("action_by")
        var actionBy: ActionBy? = null,

        @Expose
        @SerializedName("createdAt")
        private var createdAt: String = "2020-08-10T12:35:05.485Z",

        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String = "2020-08-10T12:35:05.485Z",

        @Expose
        @SerializedName("__v")
        var V: Int = 1,

        @Expose
        @SerializedName("title")
        var title: String = "Title",

        @Expose
        @SerializedName("message")
        var message: String = "message") {

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

        data class ActionBy(
            @Expose
            @SerializedName("user_image")
            val userImage: String,

            @Expose
            @SerializedName("_id")
            val Id: String,

            @Expose
            @SerializedName("first_name")
            val firstName: String,

            @Expose
            @SerializedName("last_name")
            val lastName: String,

            @Expose
            @SerializedName("user_image_url")
            val userImageUrl: String,

            @Expose
            @SerializedName("user_image_thumb_url")
            val userImageThumbUrl: String,

            @Expose
            @SerializedName("qr_code_image_url")
            val qrCodeImageUrl: String)
    }
}
