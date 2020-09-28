package com.app.ia.local

import com.app.ia.model.LoginResponse

/**
 * Created by umeshk on 07/07/19.
 */

interface PreferencesHelper {

    var deviceToken: String

    var language: String

    var userData: LoginResponse

    var notificationCount: Int

    var showIntroScreen: Boolean

    var isFirstRun: Boolean

    var securityPin: String
    
    var allowNotification: Int
    
    var enableSecurityPin: Int

    var authToken: String

    var userImage: String

    var qrCodeImage: String

    var userID: String

    var availableWalletBalance: String

    var firstName: String

    var lastName: String

    var userRole: String

    var countryCode: String

    var phone: String

    var email: String

    var mCurrentLat: Double

    var mCurrentLng: Double

    var cartItemCount : Int

}