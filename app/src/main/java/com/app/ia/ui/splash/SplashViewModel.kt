package com.app.ia.ui.splash

import android.app.Activity
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySplashBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.login.LoginActivity
import com.app.ia.utils.Coroutines
import com.app.ia.utils.startActivity
import kotlinx.coroutines.delay

@Suppress("UNUSED_PARAMETER")
class SplashViewModel(baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivitySplashBinding

    fun setVariable(mBinding: ActivitySplashBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun callNextActivity() {
        Coroutines.io {
            delay(3000)
            when {
                AppPreferencesHelper.getInstance().authToken.trim().isEmpty() -> {
                    mActivity.startActivity<LoginActivity>()
                }
                else -> {
                    mActivity.startActivity<HomeActivity>()
                }
            }
            mActivity.finish()
        }
    }
}