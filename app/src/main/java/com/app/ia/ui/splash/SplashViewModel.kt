package com.app.ia.ui.splash

import android.app.Activity
import android.text.TextUtils
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivitySplashBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.login.LoginActivity
import com.app.ia.ui.signup.SignUpActivity
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
            delay(500)
            if (AppPreferencesHelper.getInstance().isFirstRun) {
                AppPreferencesHelper.getInstance().isFirstRun = false
                mActivity.startActivity<LoginActivity>()
            } else if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
                mActivity.startActivity<HomeActivity>()
            } else {
                /*if (AppPreferencesHelper.getInstance().enableSecurityPin == 1) {
                    mActivity.startActivity<EnterPasscodeActivity> {
                        putExtra(AppConstants.EXTRA_IS_FROM_SPLASH, true)
                    }
                } else {
                    mActivity.startActivity<HomeActivity>()
                }*/

            }
            mActivity.finish()
        }
    }
}