package com.app.ia.local

import com.app.ia.model.AddressListResponse
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

    var allowNotification: Int
    
    var authToken: String

    var userImage: String

    var userID: String

    var userName: String

    var countryCode: String

    var phone: String

    var email: String

    var mCurrentLat: Double

    var mCurrentLng: Double

    var cartItemCount : Int

    var walletAmount : String

    var defaultAddress : AddressListResponse.AddressList
}