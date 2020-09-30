package com.app.ia.apiclient

import com.app.ia.enums.ENVIRONMENT

object Api {

    var BASE_URL = getBaseUrl(ENVIRONMENT.DEVELOPMENT.ordinal)

    private const val COMMON_PATH = "/user_service/customer/"

    const val LOGIN_URL = COMMON_PATH + "user_login"
    const val REGISTER_URL = COMMON_PATH + "registration"
    const val VERIFY_OTP = COMMON_PATH + "verify_otp"
    const val RESEND_OTP = COMMON_PATH + "resend_otp"

    const val UPDATE_FORGOT_PASSWORD_URL = COMMON_PATH + "update_forgot_password"

    private fun getBaseUrl(environmentType: Int): String {

        return when (environmentType) {

            ENVIRONMENT.LOCAL.ordinal -> {
                "http://192.168.1.69:3050/"
            }

            ENVIRONMENT.DEVELOPMENT.ordinal -> {
                "http://13.235.188.102:3060/"
            }

            ENVIRONMENT.TESTING.ordinal -> {
                "http://13.235.188.102:3060/"
            }

            ENVIRONMENT.LIVE.ordinal -> {
                "http://13.235.188.102:3060/"
            }

            else -> {
                "http://13.235.188.102:3060/"
            }
        }
    }
}