package com.app.ia.apiclient

import com.app.ia.enums.ENVIRONMENT

object Api {

    var BASE_URL = getBaseUrl(ENVIRONMENT.DEVELOPMENT.ordinal)

    private const val COMMON_PATH = "/user_service/customer/"
    private const val CATEGORY_PATH = "/user_service/customer/product/"

    const val LOGIN_URL = COMMON_PATH + "login"
    const val REGISTER_URL = COMMON_PATH + "registration"
    const val VERIFY_OTP = COMMON_PATH + "verify_otp"
    const val RESEND_OTP = COMMON_PATH + "resend_otp"

    const val UPDATE_FORGOT_PASSWORD_URL = COMMON_PATH + "update_forgot_password"

    const val BUSINESS_CATEGORY = CATEGORY_PATH + "get_business_category"
    const val PRODUCT_CATEGORY = CATEGORY_PATH + "get_category"
    const val PRODUCT_SUB_CATEGORY = CATEGORY_PATH + "get_subcategory"
    const val PRODUCT_LISTING = CATEGORY_PATH + "get_products"
    const val SIMILAR_PRODUCT_LISTING = CATEGORY_PATH + "similar_products"

    const val PRODUCT_DETAIL = CATEGORY_PATH + "get_product_detail"
    const val CUSTOMIZATION_PRODUCT = CATEGORY_PATH + "other_customization_products"
    const val FAVORITE_LISTING = CATEGORY_PATH + "get_favourite_items"
    const val BANNER_LISTING = CATEGORY_PATH + "banner/get_banners"
    const val DISCOUNT_PRODUCT_LISTING = CATEGORY_PATH + "discountproduct"
    const val POPULAR_PRODUCT_LISTING = CATEGORY_PATH + "popularproduct"

    const val BRAND = CATEGORY_PATH + "brand/get_brands"
    const val FAVOURITE_PRODUCT = CATEGORY_PATH + "add_favourite_item"

    const val GET_TRANSACTION_HISTORY = COMMON_PATH + "wallet/get_transaction_history"
    const val ADD_TO_WALLET = COMMON_PATH + "wallet/add_to_wallet"

    const val ADD_ADDRESS = COMMON_PATH + "address/create_address"
    const val DELETE_ADDRESS = COMMON_PATH + "address/delete_address"
    const val SET_DEFAULT_ADDRESS = COMMON_PATH + "address/set_default_address"
    const val ADDRESS_LIST = COMMON_PATH + "address/get_addresses"

    const val GET_PROFILE_URL = COMMON_PATH + "profile/get_profile"
    const val CHANGE_PASSWORD_URL = COMMON_PATH + "profile/change_password"
    const val UPDATE_PROFILE_URL = COMMON_PATH + "profile/update_profile"

    const val NOTIFICATION_URL = COMMON_PATH + "notifications"
    const val DELETE_NOTIFICATION_URL = COMMON_PATH + "notifications/{notification_id}"
    const val CONTENT_DATA_URL = COMMON_PATH + "contents/{content_key}"

    ///user_service/customer/product/customizationtype/:category_id
    const val GET_CUSTOMIZATION_TYPE_URL = CATEGORY_PATH + "customizationtype/{category_id}"
    const val GET_CUSTOMIZATION_SUB_TYPE_URL = CATEGORY_PATH + "customizationsubtype/{customizationtype_id}"

    /*Add To Cart*/
    const val ADD_TO_CART = COMMON_PATH + "cart" //Post
    const val CART_LIST = COMMON_PATH + "cart" //Get
    const val UPDATE_CART_ITEM = COMMON_PATH + "cart" //Update
    const val DELETE_CART_ITEM = COMMON_PATH + "cart/{cart_id}" //Delete
    const val PLACE_ORDER = COMMON_PATH + "order/checkout"
    const val ORDER_HISTORY = COMMON_PATH + "order/user_orders"
    const val ORDER_PAST_HISTORY = COMMON_PATH + "order/past_orders"
    const val NOTIFY_ME = CATEGORY_PATH + "notifyme"

    const val ORDER_DETAIL = COMMON_PATH + "order/order_details/{order_id}" //Get

    //Offers
    const val OFFER_LIST = COMMON_PATH + "offer"
    const val GET_DELIVERY_FEE = COMMON_PATH + "order/calculate_delivery_fees"
    const val APPLY_COUPON = COMMON_PATH + "offer/apply_coupon"

    const val CANCEL_GROCERY_ORDER = COMMON_PATH + "orders/cancel_grocery_order"
    const val CANCEL_ORDER = COMMON_PATH + "orders/cancel_order"

    const val RETURN_GROCERY_ORDER = COMMON_PATH + "orders/grocery_return_request"
    const val RETURN_ORDER = COMMON_PATH + "orders/return_request"
    const val GET_CART_COUNT = COMMON_PATH + "cart/get_cart_item_count"

    const val RATING_REVIEW = CATEGORY_PATH + "ratings"
    const val REDEEM_POINTS = COMMON_PATH + "order/get_redeem_point"

    const val GET_ADDRESS_FROM_INPUT = "https://maps.googleapis.com/maps/api/place/autocomplete/"

    const val REDIRECT_URL = "http://3.7.83.168:3060" + COMMON_PATH + "payment/complete_payment"//"http://122.182.6.216/merchant/ccavResponseHandler.jsp"
    const val CANCEL_URL = "http://3.7.83.168:3060" + COMMON_PATH + "payment/complete_payment"//"http://122.182.6.216/merchant/ccavResponseHandler.jsp"
    const val RSA_URL =  "https://secure.ccavenue.com/transaction/getRSAKey"
    //const val RSA_URL =  "https://test.ccavenue.com/transaction/getRSAKey"

    const val GENERATE_ORDER = COMMON_PATH + "order/genrate_online_order"
    const val CHECK_PAYMENT_STATUS = COMMON_PATH + "payment/check_status"

    //Deal of the day banner
    const val DEAL_DAY_BANNER_LISTING = CATEGORY_PATH + "dealofday"
    const val DEAL_DAY_BANNER_DETAIL  = CATEGORY_PATH + "dealofday/{banner_id}"
    const val DOWNLOAD_INVOICE = COMMON_PATH + "order/downloadOrderInvoice"


    private fun getBaseUrl(environmentType: Int): String {

        return when (environmentType) {

            ENVIRONMENT.LOCAL.ordinal -> {
                "http://192.168.1.78:3050/"
            }

            ENVIRONMENT.DEVELOPMENT.ordinal -> {
                "http://3.7.83.168:3060/"
            }

            ENVIRONMENT.TESTING.ordinal -> {
                "http://192.168.1.69:3050/"
            }

            ENVIRONMENT.LIVE.ordinal -> {
                "http://13.235.188.102:3063/"
            }

            else -> {
                "http://13.235.188.102:3060/"
            }
        }
    }
}