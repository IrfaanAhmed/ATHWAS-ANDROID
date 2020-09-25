package com.app.ia.local

import android.content.Context
import android.content.SharedPreferences
import com.app.ia.IAApplication
import com.app.ia.utils.AppConstants

/**
 * Created by umeshk on 07/07/19.
 */

class AppPreferencesHelper internal constructor() : PreferencesHelper {

    private val sharedPreferences: SharedPreferences = IAApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override var authToken: String
        get() = sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, AppConstants.EMPTY_STRING)!!
        set(accessToken) = sharedPreferences.edit().putString(PREF_KEY_AUTH_TOKEN, accessToken).apply()

    override var deviceToken: String
        get() = sharedPreferences.getString(PREF_KEY_DEVICE_TOKEN, AppConstants.EMPTY_STRING)!!
        set(deviceToken) = sharedPreferences.edit().putString(PREF_KEY_DEVICE_TOKEN, deviceToken).apply()

    override var language: String
        get() = sharedPreferences.getString(PREF_KEY_LANGUAGE, "en")!!
        set(language) = sharedPreferences.edit().putString(PREF_KEY_LANGUAGE, language).apply()

    override var email: String
        get() = sharedPreferences.getString(PREF_KEY_USER_EMAIL, AppConstants.EMPTY_STRING)!!
        set(email) = sharedPreferences.edit().putString(PREF_KEY_USER_EMAIL, email).apply()

    override var userID: String
        get() = sharedPreferences.getString(PREF_KEY_USER_ID, AppConstants.EMPTY_STRING)!!
        set(userId) = sharedPreferences.edit().putString(PREF_KEY_USER_ID, userId).apply()

    override var firstName: String
        get() = sharedPreferences.getString(PREF_KEY_FIRST_NAME, AppConstants.EMPTY_STRING)!!
        set(userName) = sharedPreferences.edit().putString(PREF_KEY_FIRST_NAME, userName).apply()

    override var lastName: String
        get() = sharedPreferences.getString(PREF_KEY_LAST_NAME, AppConstants.EMPTY_STRING)!!
        set(userName) = sharedPreferences.edit().putString(PREF_KEY_LAST_NAME, userName).apply()

    override var userImage: String
        get() = sharedPreferences.getString(PREF_KEY_USER_IMAGE_URL, AppConstants.EMPTY_STRING)!!
        set(profilePicUrl) = sharedPreferences.edit().putString(PREF_KEY_USER_IMAGE_URL, profilePicUrl).apply()

    override var qrCodeImage: String
        get() = sharedPreferences.getString(PREF_KEY_QR_CODE_IMAGE_URL, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_QR_CODE_IMAGE_URL, value).apply()

    override var securityPin: String
        get() = sharedPreferences.getString(PREF_KEY_SECURITY_PIN, AppConstants.EMPTY_STRING)!!
        set(securityPin) = sharedPreferences.edit().putString(PREF_KEY_SECURITY_PIN, securityPin).apply()

    override var allowNotification: Int
        get() = sharedPreferences.getInt(PREF_KEY_ALLOW_NOTIFICATION, 0)
        set(allowNotification) = sharedPreferences.edit().putInt(PREF_KEY_ALLOW_NOTIFICATION, allowNotification).apply()

    override var enableSecurityPin: Int
        get() = sharedPreferences.getInt(PREF_KEY_ENABLE_SECURITY_PIN, 0)
        set(enableSecurityPin) = sharedPreferences.edit().putInt(PREF_KEY_ENABLE_SECURITY_PIN, enableSecurityPin).apply()

    override var availableWalletBalance: String
        get() = sharedPreferences.getString(PREF_KEY_WALLET_BALANCE, "0")!!
        set(availableWalletBalance) = sharedPreferences.edit().putString(PREF_KEY_WALLET_BALANCE, availableWalletBalance).apply()

    override var availableRewardPoints: String
        get() = sharedPreferences.getString(PREF_KEY_REWARD_POINTS, "0")!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_REWARD_POINTS, value).apply()

    override var phone: String
        get() = sharedPreferences.getString(PREF_KEY_USER_MOBILE, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_MOBILE, value).apply()

    override var userRole: String
        get() = sharedPreferences.getString(PREF_KEY_USER_ROLE, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_ROLE, value).apply()

    override var showIntroScreen: Boolean
        get() = sharedPreferences.getBoolean(PREF_KEY_INTRO_SCREEN, true)
        set(value) = sharedPreferences.edit().putBoolean(PREF_KEY_INTRO_SCREEN, value).apply()

    override var isFirstRun: Boolean
        get() = sharedPreferences.getBoolean(PREF_KEY_FIRST_RUN, true)
        set(value) = sharedPreferences.edit().putBoolean(PREF_KEY_FIRST_RUN, value).apply()

    override var notificationCount: Int
        get() = sharedPreferences.getInt(PREF_KEY_NOTIFICATION_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_NOTIFICATION_COUNT, value).apply()

    override var cartItemCount: Int
        get() = sharedPreferences.getInt(PREF_KEY_CART_ITEM_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(PREF_KEY_CART_ITEM_COUNT, value).apply()

    override var countryCode: String
        get() = sharedPreferences.getString(PREF_KEY_USER_COUNTRY_CODE, AppConstants.EMPTY_STRING)!!
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_USER_COUNTRY_CODE, value).apply()
        }

    override var mCurrentLat: Double
        get() = sharedPreferences.getString(PREF_KEY_CURRENT_LAT, "0.0")!!.toDouble()
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_CURRENT_LAT, value.toString()).apply()
        }

    override var mCurrentLng: Double
        get() = sharedPreferences.getString(PREF_KEY_CURRENT_LNG, "0.0")!!.toDouble()
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_CURRENT_LNG, value.toString()).apply()
        }

    /*override var userData: LoginResponse
        get() = LoginResponse(securityPin, "", authToken, 0, 0, 0, allowNotification, enableSecurityPin, "", "", "", LoginResponse.Geolocation(), availableRewardPoints, availableWalletBalance, 0, "", "", "", 0, "", "", 0, 0, userID, firstName, lastName, "", countryCode, phone, email, "", "", "", "", 0, userImage, "", qrCodeImage, userID)
        set(value) {
            authToken = value.authToken
            phone = value.phone
            userImage = value.userImageUrl
            qrCodeImage = value.qrCodeImageUrl
            firstName = value.firstName
            lastName = value.lastName
            email = value.email
            userRole = value.userRole
            userID = value.id
            securityPin = value.securityPin
            allowNotification = value.allowNotifications
            enableSecurityPin = value.enableSecurityPin
            availableWalletBalance = value.availableWalletBalance
            availableRewardPoints = value.availableRewardPoints
            countryCode = value.countryCode
        }*/

    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {

        //user credential
        private const val PREF_NAME = "com.app.tivo.preference"
        private const val PREF_KEY_AUTH_TOKEN = "AUTH_TOKEN"
        private const val PREF_KEY_DEVICE_TOKEN = "DEVICE_TOKEN"
        private const val PREF_KEY_LANGUAGE = "LANGUAGE"
        private const val PREF_KEY_USER_EMAIL = "USER_EMAIL"
        private const val PREF_KEY_USER_ID = "USER_ID"
        private const val PREF_KEY_FIRST_NAME = "FIRST_NAME"
        private const val PREF_KEY_LAST_NAME = "LAST_NAME"
        private const val PREF_KEY_USER_IMAGE_URL = "USER_PROFILE_IMAGE_URL"
        private const val PREF_KEY_QR_CODE_IMAGE_URL = "USER_QR_CODE_IMAGE_URL"
        private const val PREF_KEY_USER_COUNTRY_CODE = "USER_COUNTRY_CODE"
        private const val PREF_KEY_USER_MOBILE = "USER_MOBILE"
        private const val PREF_KEY_SECURITY_PIN = "SECURITY_PIN"
        private const val PREF_KEY_ALLOW_NOTIFICATION = "ALLOW_NOTIFICATION"
        private const val PREF_KEY_ENABLE_SECURITY_PIN = "ENABLE_SECURITY_PIN"
        private const val PREF_KEY_WALLET_BALANCE = "WALLET_BALANCE"
        private const val PREF_KEY_REWARD_POINTS = "REWARD_POINTS"
        private const val PREF_KEY_USER_ROLE = "USER_ROLE"
        private const val PREF_KEY_CART_ITEM_COUNT = "CART_ITEM_COUNT"

        private const val PREF_KEY_NOTIFICATION_COUNT = "NOTIFICATION_COUNT"
        private const val PREF_KEY_INTRO_SCREEN = "INTRO_SCREEN"
        private const val PREF_KEY_FIRST_RUN = "IS_FIRST_RUN"

        //Location Related
        private const val PREF_KEY_CURRENT_LAT = "CURRENT_LAT"
        private const val PREF_KEY_CURRENT_LNG = "CURRENT_LNG"

        private var appPreferencesHelper: AppPreferencesHelper? = null

        fun getInstance(): AppPreferencesHelper {
            if (appPreferencesHelper == null) {
                appPreferencesHelper = AppPreferencesHelper()
                return appPreferencesHelper!!
            }
            return appPreferencesHelper!!
        }
    }
}