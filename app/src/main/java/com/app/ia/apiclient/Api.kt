package com.app.ia.apiclient

import com.app.ia.enums.ENVIRONMENT

object Api {

    var BASE_URL = getBaseUrl(ENVIRONMENT.DEVELOPMENT.ordinal)

    private const val COMMON_PATH = "user_service/merchant/"

    const val LOGIN_URL = COMMON_PATH + "profile/login"

    private fun getBaseUrl(environmentType: Int): String {

        return when (environmentType) {

            ENVIRONMENT.LOCAL.ordinal -> {
                "http://192.168.1.154:3030/"
            }

            ENVIRONMENT.DEVELOPMENT.ordinal -> {
                "http://13.235.188.102:3030"
            }

            ENVIRONMENT.TESTING.ordinal -> {
                "https://72.octaldevs.com/cleverapp/public/api/"
            }

            ENVIRONMENT.LIVE.ordinal -> {
                "https://www.cleverksa.com/api/"
            }

            else -> {
                "https://www.cleverksa.com/api/"
            }
        }
    }
}