package com.app.ia.local

import android.content.Context
import android.content.SharedPreferences
import com.app.ia.IAApplication
import com.app.ia.model.LoginResponse
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

    override var userName: String
        get() = sharedPreferences.getString(PREF_KEY_USER_NAME, AppConstants.EMPTY_STRING)!!
        set(userName) = sharedPreferences.edit().putString(PREF_KEY_USER_NAME, userName).apply()

    override var userImage: String
        get() = sharedPreferences.getString(PREF_KEY_USER_IMAGE_URL, AppConstants.EMPTY_STRING)!!
        set(profilePicUrl) = sharedPreferences.edit().putString(PREF_KEY_USER_IMAGE_URL, profilePicUrl).apply()

    override var allowNotification: Int
        get() = sharedPreferences.getInt(PREF_KEY_ALLOW_NOTIFICATION, 1)
        set(allowNotification) = sharedPreferences.edit().putInt(PREF_KEY_ALLOW_NOTIFICATION, allowNotification).apply()

    override var phone: String
        get() = sharedPreferences.getString(PREF_KEY_USER_MOBILE, AppConstants.EMPTY_STRING)!!
        set(value) = sharedPreferences.edit().putString(PREF_KEY_USER_MOBILE, value).apply()

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

    override var walletAmount: String
        get() = sharedPreferences.getString(PREF_KEY_WALLET_AMOUNT, "0")!!
        set(value) {
            sharedPreferences.edit().putString(PREF_KEY_WALLET_AMOUNT, value).apply()
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

    override var userData: LoginResponse
        get() = getLoginData()
        set(value) {
            authToken = value.authToken
            phone = value.phone
            userImage = value.userImageUrl
            userName = value.username
            email = value.email
            userID = value._Id
            countryCode = value.countryCode
        }

    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    fun getString(KEY_DATA: String): String {
        return sharedPreferences.getString(KEY_DATA, AppConstants.EMPTY_STRING)!!
    }

    fun getString(KEY_DATA: String, defaultValue: String): String {
        return sharedPreferences.getString(KEY_DATA, defaultValue)!!
    }

    fun setString(KEY: String, data: String) {
        sharedPreferences.edit().putString(KEY, data).apply()
    }

    private fun getLoginData(): LoginResponse {
        val loginResponse = LoginResponse()
        loginResponse.authToken = authToken
        loginResponse.phone = phone
        loginResponse.userImage = userImage
        loginResponse.username = userName
        loginResponse.email = email
        loginResponse._Id = userID
        loginResponse.countryCode = countryCode
        return loginResponse
    }

    companion object {

        //user credential
        private const val PREF_NAME = "com.app.ia.preference"
        private const val PREF_KEY_AUTH_TOKEN = "AUTH_TOKEN"
        private const val PREF_KEY_DEVICE_TOKEN = "DEVICE_TOKEN"
        private const val PREF_KEY_LANGUAGE = "LANGUAGE"
        private const val PREF_KEY_USER_EMAIL = "USER_EMAIL"
        private const val PREF_KEY_USER_ID = "USER_ID"
        private const val PREF_KEY_USER_NAME = "USER_NAME"
        private const val PREF_KEY_USER_IMAGE_URL = "USER_PROFILE_IMAGE_URL"
        private const val PREF_KEY_USER_COUNTRY_CODE = "USER_COUNTRY_CODE"
        private const val PREF_KEY_USER_MOBILE = "USER_MOBILE"
        private const val PREF_KEY_ALLOW_NOTIFICATION = "ALLOW_NOTIFICATION"
        private const val PREF_KEY_CART_ITEM_COUNT = "CART_ITEM_COUNT"

        private const val PREF_KEY_NOTIFICATION_COUNT = "NOTIFICATION_COUNT"
        private const val PREF_KEY_INTRO_SCREEN = "INTRO_SCREEN"
        private const val PREF_KEY_FIRST_RUN = "IS_FIRST_RUN"
        private const val PREF_KEY_WALLET_AMOUNT = "WALLET_AMOUNT"

        //Location Related
        private const val PREF_KEY_CURRENT_LAT = "CURRENT_LAT"
        private const val PREF_KEY_CURRENT_LNG = "CURRENT_LNG"

        private var appPreferencesHelper: AppPreferencesHelper? = null

        const val CATEGORY = "CATEGORY"

        fun getInstance(): AppPreferencesHelper {
            if (appPreferencesHelper == null) {
                appPreferencesHelper = AppPreferencesHelper()
                return appPreferencesHelper!!
            }
            return appPreferencesHelper!!
        }
    }
}