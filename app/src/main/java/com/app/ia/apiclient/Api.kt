package com.app.ia.apiclient

import com.app.ia.enums.ENVIRONMENT

object Api {

    var BASE_URL = getBaseUrl(ENVIRONMENT.DEVELOPMENT.ordinal)

    private const val COMMON_PATH = "/user_service/customer/"
    private const val CATEGORY_PATH = "/user_service/customer/product/"

    const val LOGIN_URL = COMMON_PATH + "user_login"
    const val REGISTER_URL = COMMON_PATH + "registration"
    const val VERIFY_OTP = COMMON_PATH + "verify_otp"
    const val RESEND_OTP = COMMON_PATH + "resend_otp"

    const val UPDATE_FORGOT_PASSWORD_URL = COMMON_PATH + "update_forgot_password"

    const val BUSINESS_CATEGORY = CATEGORY_PATH + "get_business_category"
    const val PRODUCT_CATEGORY = CATEGORY_PATH + "get_category"
    const val PRODUCT_SUB_CATEGORY = CATEGORY_PATH + "get_subcategory"
    const val PRODUCT_LISTING = CATEGORY_PATH + "get_products"

    const val FAVORITE_LISTING = CATEGORY_PATH + "get_favourite_items"

    const val BRAND = CATEGORY_PATH + "brand/get_brands"
    const val FAVOURITE_PRODUCT = CATEGORY_PATH + "add_favourite_item"

    const val GET_TRANSACTION_HISTORY = COMMON_PATH + "wallet/get_transaction_history"
    const val ADD_TO_WALLET = COMMON_PATH + "wallet/add_to_wallet"

    const val ADD_ADDRESS = COMMON_PATH + "address/create_address"
    const val DELETE_ADDRESS = COMMON_PATH + "address/delete_address"
    const val ADDRESS_LIST = COMMON_PATH + "address/get_addresses"

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