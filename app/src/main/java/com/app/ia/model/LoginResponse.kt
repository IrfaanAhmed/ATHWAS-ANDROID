package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LoginResponse(

    @Expose
    @SerializedName("online_status")
    var onlineStatus: Int = 0,
    @Expose
    @SerializedName("is_user_verified")
    var isUserVerified: Int = 0,
    @Expose
    @SerializedName("is_email_verified")
    var isEmailVerified: Int = 0,
    @Expose
    @SerializedName("auth_token")
    var authToken: String = "",
    @Expose
    @SerializedName("_id")
    var _Id: String = "",
    @Expose
    @SerializedName("username")
    var username: String = "",
    @Expose
    @SerializedName("country_code")
    var countryCode: String = "",
    @Expose
    @SerializedName("phone")
    var phone: String = "",
    @Expose
    @SerializedName("email")
    var email: String = "",
    @Expose
    @SerializedName("user_image")
    var userImage: String = "",
    @Expose
    @SerializedName("register_id")
    var registerId: String = "",
    @Expose
    @SerializedName("user_image_url")
    var userImageUrl: String = "",
    @Expose
    @SerializedName("user_image_thumb_url")
    var userImageThumbUrl: String = "",
    @Expose
    @SerializedName("id")
    var id: String = "")
