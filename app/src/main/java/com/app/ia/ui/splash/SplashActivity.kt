package com.app.ia.ui.splash

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.R
import com.app.ia.base.BaseActivity
import com.app.ia.databinding.ActivitySplashBinding
import com.app.ia.BR
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseRepository
import com.app.ia.dialog.IADialog
import com.app.ia.local.AppPreferencesHelper

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    private var mBinding: ActivitySplashBinding? = null
    private var mViewModel: SplashViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getViewModel(): SplashViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        storeDeviceToken()
        currentLocationManager(false)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SplashViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
    }

    private fun storeDeviceToken() {
        /*FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            AppLogger.d("device token : $deviceToken")
            AppPreferencesHelper.getInstance().deviceToken = deviceToken
        }*/
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {

        if (latitude == 0.0 && longitude == 0.0) {
            IADialog(this, getString(R.string.unable_fetch_location), true)
        } else {
            AppPreferencesHelper.getInstance().mCurrentLat = latitude
            AppPreferencesHelper.getInstance().mCurrentLng = longitude

            mViewModel?.callNextActivity()
        }
    }

    override fun onLocationUpdate(locationResult: Location?) {
        super.onLocationUpdate(locationResult)
        if (locationResult != null) {
            if (locationResult.latitude != 0.0 && locationResult.longitude != 0.0) {
                mLocationManager?.removeLocationUpdate()
                AppPreferencesHelper.getInstance().mCurrentLat = locationResult.latitude
                AppPreferencesHelper.getInstance().mCurrentLng = locationResult.longitude

                mViewModel?.callNextActivity()
            } else {
                IADialog(this, getString(R.string.unable_fetch_location), true)
            }
        } else {
            IADialog(this, getString(R.string.unable_fetch_location), true)
        }
    }

    override fun onLocationFetchFailed() {
        super.onLocationFetchFailed()
        mLocationManager?.startLocationUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            mLocationManager?.removeLocationUpdate()
        }
    }
}