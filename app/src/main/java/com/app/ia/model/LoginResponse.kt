package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @Expose @SerializedName("security_pin") var securityPin: String = "",
    @Expose @SerializedName("referral_code") var referralCode: String = "",
    @Expose @SerializedName("auth_token") var authToken: String = "",
    @Expose @SerializedName("is_user_verified") var isUserVerified: Int = 0,
    @Expose @SerializedName("is_phone_verified") var isPhoneVerified: Int = 0,
    @Expose @SerializedName("is_email_verified") var isEmailVerified: Int = 0,
    @Expose @SerializedName("allow_notifications") var allowNotifications: Int = 0,
    @Expose @SerializedName("enable_security_pin") var enableSecurityPin: Int = 0,
    @Expose @SerializedName("qr_code_image") var qrCodeImage: String = "",
    @Expose @SerializedName("address") var address: String = "",
    @Expose @SerializedName("is_active") var isActive: Int = 0,
    @Expose @SerializedName("is_deleted") var isDeleted: Int = 0,
    @Expose @SerializedName("user_image") var userImage: String = "",
    @Expose @SerializedName("available_wallet_balance") var availableWalletBalance: String = "",
    @Expose @SerializedName("total_orders_count") var totalOrdersCount: Int = 0,
    @Expose @SerializedName("default_payment_option") var defaultPaymentOption: String = "",
    @Expose @SerializedName("default_country") var defaultCountry: String = "",
    @Expose @SerializedName("default_language") var defaultLanguage: String = "",
    @Expose @SerializedName("kyc_reference_id") var kycReferenceId: String = "",
    @Expose @SerializedName("kyc_varidation_url") var kycvaridationUrl: String = "",
    @Expose @SerializedName("is_kyc_done") var isKycDone: Int = 0,
    @Expose @SerializedName("_id") var Id: String = "",
    @Expose @SerializedName("first_name") var firstName: String = "",
    @Expose @SerializedName("last_name") var lastName: String = "",
    @Expose @SerializedName("user_role") var userRole: String = "",
    @Expose @SerializedName("country_code") var countryCode: String = "",
    @Expose @SerializedName("phone") var phone: String = "",
    @Expose @SerializedName("email") var email: String = "",
    @Expose @SerializedName("password") var password: String = "",
    @Expose @SerializedName("last_login_time") var lastLoginTime: String = "",
    @Expose @SerializedName("geoLocation") var geolocation: Geolocation? = null,
    @Expose @SerializedName("createdAt") var createdAt: String = "",
    @Expose @SerializedName("updatedAt") var updatedAt: String = "",
    @Expose @SerializedName("__v") var V: Int = 0,
    @Expose @SerializedName("user_image_url") var userImageUrl: String = "",
    @Expose @SerializedName("user_image_thumb_url") var userImageThumbUrl: String = "",
    @Expose @SerializedName("qr_code_image_url") var qrCodeImageUrl: String = "",
    @Expose @SerializedName("id") var _id: String = "",
    @Expose @SerializedName("otp_number") var otpNumber: String = "") {

    data class Geolocation(@Expose @SerializedName("type") var type: String, @Expose @SerializedName("coordinates") var coordinates: List<String>, @Expose @SerializedName("_id") var Id: String)
}