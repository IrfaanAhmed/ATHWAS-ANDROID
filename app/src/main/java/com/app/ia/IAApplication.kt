package com.app.ia

import android.app.Activity
import android.app.Application
import com.app.ia.utils.AppLogger
import com.google.firebase.FirebaseApp


class IAApplication : Application() {

    private var mCurrentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        iaApplication = this
        AppLogger.init()
        FirebaseApp.initializeApp(this)
        //initPlace()
    }

    companion object {

        private var iaApplication: IAApplication? = null
        fun getInstance(): IAApplication {
            return iaApplication!!
        }
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    /*private fun initPlace() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GoogleMapKey, Locale.US)
        }
    }*/
}