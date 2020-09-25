package com.app.ia

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.ia.utils.AppLogger


class IAApplication : Application(), LifecycleObserver {

    private var mCurrentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        iaApplication = this
        AppLogger.init()
        //FirebaseApp.initializeApp(this)
        //initPlace()
        //ProcessLifecycleOwner.get().lifecycle.addObserver(this)
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun appInResumeState() {
        Toast.makeText(this, "In Foreground", Toast.LENGTH_LONG).show()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun appInPauseState() {
        Toast.makeText(this, "In Background", Toast.LENGTH_LONG).show()
    }
}