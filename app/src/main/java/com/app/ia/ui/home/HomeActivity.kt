package com.app.ia.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityHomeBinding
import com.app.ia.helper.CardDrawerLayout
import com.app.ia.model.AddressListResponse
import com.app.ia.ui.my_order.MyOrdersFragment
import com.app.ia.ui.wallet.WalletFragment
import com.app.ia.utils.*
import com.app.ia.utils.CommonUtils.getAddressFromLocation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar_home.view.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    private var mActivityBinding: ActivityHomeBinding? = null
    private var mViewModel: HomeViewModel? = null

    private var mDrawer: CardDrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var isNotificationPending = false

    companion object {
        const val KEY_REDIRECTION = "KEY_REDIRECTION"
        const val KEY_REDIRECTION_ID = "KEY_REDIRECTION_ID"
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun getViewModel(): HomeViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)
        setSupportActionBar(toolbar)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
        currentLocationManager()

        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle!!)

        drawer_layout.setViewScale(Gravity.START, 0.9f)
        drawer_layout.setViewElevation(Gravity.START, 20.0f)

        /*toolbar.bgLogo.setOnClickListener {
            toolbar.imgLogo.isSelected = true
            Handler(Looper.myLooper()!!).postDelayed({
                toolbar.imgLogo.isSelected = false
            }, 100)
        }

        toolbar.imgLogo.setOnClickListener {
            toolbar.bgLogo.isSelected = true
            Handler(Looper.myLooper()!!).postDelayed({
                toolbar.bgLogo.isSelected = false
            }, 100)
        }*/

        replaceFragment(HomeFragment.newInstance())

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@HomeActivity)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_REFRESH_ON_NOTIFICATION)
        localBroadcastReceiver.registerReceiver(refreshListener, intentFilter)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(HomeViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString())
        fragmentTransaction.commit()
    }

    private fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) return fragment
        }
        return null
    }

    fun setupHeader(title: String, isHomeScreen: Boolean) {
        mViewModel?.isHomeScreen?.set(isHomeScreen)
        mViewModel?.title?.set(title)
    }

    override fun onResume() {
        super.onResume()
        CommonUtils.showCartItemCount(toolbar.bottom_navigation_notification)
        CommonUtils.showNotificationCount(toolbar.txtNotificationCount)
        if (isNotificationPending) {
            isNotificationPending = false
            val currentFragment = getVisibleFragment()
            if (currentFragment != null) {
                if (currentFragment is MyOrdersFragment) {
                    currentFragment.resetOrderHistory()
                }
            }
        }
    }

    private val refreshListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            CommonUtils.showNotificationCount(toolbar.txtNotificationCount)
            if (intent!!.getBooleanExtra("refresh", false)) {
                isNotificationPending = true
                val currentFragment = getVisibleFragment()
                if (currentFragment != null) {
                    if (currentFragment is MyOrdersFragment) {
                        currentFragment.resetOrderHistory()
                    } /*else if (currentFragment is NotificationsFragment) {
                        currentFragment.resetNotification()
                    }*/
                }
            }
        }
    }

    override fun onDestroy() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@HomeActivity)
        localBroadcastReceiver.unregisterReceiver(refreshListener)
        super.onDestroy()
    }

    override fun onCurrentLocation(latitude: Double, longitude: Double) {
        super.onCurrentLocation(latitude, longitude)
        this.latitude = latitude
        this.longitude = longitude

        mViewModel?.address!!.value = getAddressFromLocation(this@HomeActivity, this.latitude, this.longitude)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_SELECT_ADDRESS && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedAddress = data.getSerializableExtra(AppConstants.EXTRA_SELECTED_ADDRESS) as AddressListResponse.AddressList
                mViewModel?.addressTitle!!.value = selectedAddress.addressType
                mViewModel?.address!!.value = selectedAddress.fullAddress
            }
        } else if (requestCode == AppRequestCode.REQUEST_ORDER_STATUS && resultCode == RESULT_OK) {
            if (getVisibleFragment() is MyOrdersFragment) {
                (getVisibleFragment() as MyOrdersFragment).onActivityResult(requestCode, resultCode, data)
            }
        } else if (requestCode == AppRequestCode.REQUEST_PAYMENT_CODE && resultCode == RESULT_OK) {
            if (getVisibleFragment() is WalletFragment) {
                (getVisibleFragment() as WalletFragment).onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}