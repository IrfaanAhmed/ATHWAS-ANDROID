package com.app.ia.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("otp_number")
    @Expose
    val otpNumber: String = ""
)