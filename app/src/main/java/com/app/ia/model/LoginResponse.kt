package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @Expose
    @SerializedName("merchant_id")
    val merchantId: String,

    @Expose
        @SerializedName("security_pin")
        val securityPin: String,

    @Expose
    @SerializedName("auth_token")
    val authToken: String,

    @Expose
    @SerializedName("is_user_verified")
    val isUserVerified: Int,

    @Expose
    @SerializedName("is_phone_verified")
    val isPhoneVerified: Int,

    @Expose
    @SerializedName("is_email_verified")
    val isEmailVerified: Int,

    @Expose
    @SerializedName("allow_notifications")
    val allowNotifications: Int,

    @Expose
    @SerializedName("enable_security_pin")
    val enableSecurityPin: Int,

    @Expose
    @SerializedName("business_image")
    val userImage: String,

    @Expose
    @SerializedName("qr_code_image")
    val qrCodeImage: String,

    @Expose
    @SerializedName("shop_address")
    val address: String,

    @Expose
    @SerializedName("license_no")
    val licenseNo: String,

    @Expose
    @SerializedName("description")
    val description: String,

    @Expose
    @SerializedName("total_payment_received")
    val totalPaymentReceived: Int,

    @Expose
    @SerializedName("is_active")
    val isActive: Int,

    @Expose
    @SerializedName("is_deleted")
    val isDeleted: Int,

    @Expose
    @SerializedName("available_wallet_balance")
    val availableWalletBalance: String,

    @Expose
    @SerializedName("total_orders_count")
    val totalOrdersCount: Int,

    @Expose
    @SerializedName("is_kyc_done")
    val isKycDone: Int,

    @Expose
    @SerializedName("kyc_reference_id")
    val kycReferenceId: String,

    @Expose
    @SerializedName("kyc_validation_url")
    val kycValidationUrl: String,

    @Expose
    @SerializedName("default_payment_option")
    val defaultPaymentOption: String,

    @Expose
    @SerializedName("default_country")
    val defaultCountry: String,

    @Expose
    @SerializedName("default_language")
    val defaultLanguage: String,

    @Expose
    @SerializedName("_id")
    val Id: String,

    @Expose
    @SerializedName("business_name")
    val businessName: String,

    @Expose
    @SerializedName("first_name")
    val firstName: String,

    @Expose
    @SerializedName("last_name")
    val lastName: String,

    @Expose
    @SerializedName("user_role")
    val userRole: String,

    @Expose
    @SerializedName("country_code")
    val countryCode: String,

    @Expose
    @SerializedName("phone")
    val phone: String,

    @Expose
    @SerializedName("email")
    val email: String,

    @Expose
    @SerializedName("user_image_url")
    var userImageUrl: String = "",

    @Expose
    @SerializedName("user_image_thumb_url")
    var userThumbUrl: String = "",

    @Expose
    @SerializedName("password")
    var password: String = "",

    @Expose
    @SerializedName("last_login_time")
    var lastLoginTime: String = "",

    @Expose
    @SerializedName("createdAt")
    var createdAt: String = "",
    @Expose
    @SerializedName("updatedAt")
    var updatedAt: String = "",

    @Expose
    @SerializedName("business_image_url")
    var businessImageUrl: String = "",

    @Expose
    @SerializedName("business_image_thumb_url")
    var businessThumbUrl: String = "",

    @Expose
    @SerializedName("qr_code_image_url")
    var qrCodeImageUrl: String = "",

    @Expose
    @SerializedName("otp_number")
    val otpNumber: String = "",

    @Expose
    @SerializedName("__v")
    var V: Int = 0,

    @Expose
    @SerializedName("service_category")
    var serviceCategory: ServiceCategory = ServiceCategory(),

    @Expose
    @SerializedName("service_sub_category")
    var serviceSubCategory: ServiceSubCategory = ServiceSubCategory(),

    @Expose
    @SerializedName("shop_address_cords")
    var geolocation: Geolocation = Geolocation() ) {

    data class ServiceCategory(
        @Expose
        @SerializedName("_id")
        var catId: String = "",

        @Expose
        @SerializedName("category_name")
        var catName: String = "")

    data class ServiceSubCategory(
        @Expose
        @SerializedName("_id")
        var subCatId: String = "",

        @Expose
        @SerializedName("category_name")
        var subCatName: String = "")

    data class Geolocation(
        @Expose
        @SerializedName("type")
        val type: String = "",

        @Expose
        @SerializedName("coordinates")
        val coordinates: List<String> = ArrayList(),

        @Expose
        @SerializedName("_id")
        val Id: String = "")
}