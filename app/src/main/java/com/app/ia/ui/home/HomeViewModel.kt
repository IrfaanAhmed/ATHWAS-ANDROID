package com.app.ia.ui.home

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.R
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityHomeBinding
import com.app.ia.enums.ContentType
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.LoginResponse
import com.app.ia.ui.business_category.CategoryFragment
import com.app.ia.ui.delivery_address.DeliveryAddressActivity
import com.app.ia.ui.faq.FaqActivity
import com.app.ia.ui.home.adapter.NavigationListAdapter
import com.app.ia.ui.login.LoginActivity
import com.app.ia.ui.my_cart.MyCartActivity
import com.app.ia.ui.my_order.MyOrdersFragment
import com.app.ia.ui.my_profile.ProfileActivity
import com.app.ia.ui.notification.NotificationActivity
import com.app.ia.ui.offers.OffersFragment
import com.app.ia.ui.wallet.WalletFragment
import com.app.ia.ui.webview.WebViewActivity
import com.app.ia.ui.wishlist.WishListActivity
import com.app.ia.utils.*
import com.app.ia.utils.AppConstants.EXTRA_IS_HOME_SCREEN
import kotlinx.android.synthetic.main.nav_side_menu.*
import kotlinx.android.synthetic.main.toolbar_home.view.*
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), NavigationListAdapter.OnSideMenuItemClickListener {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityHomeBinding
    val userData = MutableLiveData<LoginResponse>()
    var userImage = MutableLiveData<String>()

    var addressTitle = MutableLiveData("Current Location")
    var address = MutableLiveData("Select Address")

    fun setVariable(mBinding: ActivityHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        userData.value = AppPreferencesHelper.getInstance().userData
        userImage.value = AppPreferencesHelper.getInstance().userImage

        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(DividerItemDecoration(mActivity, LinearLayout.VERTICAL))
        val navigationAdapter = NavigationListAdapter()
        navigationAdapter.setOnSideMenuClickListener(this)
        (mActivity as HomeActivity).recViewNavigation.adapter = navigationAdapter
        val menuList = ArrayList<String>()
        menuList.add(mActivity.getString(R.string.home))
        menuList.add(mActivity.getString(R.string.shop_by_category))
        menuList.add(mActivity.getString(R.string.offers))
        if (!TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            menuList.add(mActivity.getString(R.string.my_account))
            menuList.add(mActivity.getString(R.string.my_orders))
            menuList.add(mActivity.getString(R.string.my_wallet))
        }
        menuList.add(mActivity.getString(R.string.faq))
        menuList.add(mActivity.getString(R.string.about_us))
        menuList.add(mActivity.getString(R.string.contact_us))
        menuList.add(mActivity.getString(R.string.terms_of_use))
        menuList.add(mActivity.getString(R.string.privacy_policy2))
        menuList.add(mActivity.getString(R.string.terms_n_condition))
        if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            menuList.add(mActivity.getString(R.string.login))
        } else {
            menuList.add(mActivity.getString(R.string.logout))
        }
        navigationAdapter.submitList(menuList)

        if (AppPreferencesHelper.getInstance().authToken.isNotEmpty()) {
            cartCountObserver()
        }
    }

    fun onHumBurgerMenuClick() {
        mBinding.drawerLayout.openDrawer(mBinding.navView)
    }

    fun onSelectAddressClick() {
        if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            (mActivity as HomeActivity).loginDialog()
        }
        else{
            mActivity.mStartActivityForResult<DeliveryAddressActivity>(AppRequestCode.REQUEST_SELECT_ADDRESS) {
                putExtra(EXTRA_IS_HOME_SCREEN, true)
            }
        }
    }

    fun onCartClick() {
        if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            (mActivity as HomeActivity).loginDialog()
        } else {
            mActivity.startActivity<MyCartActivity>()
        }
    }

    fun onNotificationClick() {
        if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            (mActivity as HomeActivity).loginDialog()
        } else {
            mActivity.startActivity<NotificationActivity>()
        }
    }

    fun onWishListClick() {
        if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            (mActivity as HomeActivity).loginDialog()
        } else {
            mActivity.startActivity<WishListActivity>()
        }
    }

    override fun onSideMenuClick(position: Int) {

        if (mBinding.drawerLayout.isOpen) {
            mBinding.drawerLayout.closeDrawer(mBinding.navView)
        }

        if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            //If user is not logged In
            when (position) {
                0 -> {
                    (mActivity as HomeActivity).replaceFragment(HomeFragment.newInstance())
                }

                1 -> {
                    (mActivity as HomeActivity).replaceFragment(CategoryFragment.newInstance())
                }

                2 -> {
                    (mActivity as HomeActivity).loginDialog()
                    //(mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
                }

                3 -> {
                    (mActivity as HomeActivity).startActivity<FaqActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.faq))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.FAQ.contentType)
                    }
                }

                4 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.about_us))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.ABOUT_US.contentType)
                    }
                }

                5 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.contact_us))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.CONTACT_US.contentType)
                    }
                }

                6 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_of_use))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_OF_USE.contentType)
                    }
                }

                7 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.privacy_policy2))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.PRIVACY_POLICY.contentType)
                    }
                }

                8 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_n_condition))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_N_CONDITION.contentType)
                    }
                }

                9 -> {
                    mActivity.startActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            }
        } else {

            when (position) {
                0 -> {
                    (mActivity as HomeActivity).replaceFragment(HomeFragment.newInstance())
                }

                1 -> {
                    (mActivity as HomeActivity).replaceFragment(CategoryFragment.newInstance())
                }

                2 -> {
                    (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
                }

                3 -> {
                    mActivity.startActivity<ProfileActivity>()
                }

                4 -> {
                    (mActivity as HomeActivity).replaceFragment(MyOrdersFragment.newInstance())
                }

                5 -> {
                    (mActivity as HomeActivity).replaceFragment(WalletFragment.newInstance())
                }

                6 -> {
                    (mActivity as HomeActivity).startActivity<FaqActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.faq))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.FAQ.contentType)
                    }
                }

                7 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.about_us))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.ABOUT_US.contentType)
                    }
                }

                8 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.contact_us))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.CONTACT_US.contentType)
                    }
                }

                9 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_of_use))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_OF_USE.contentType)
                    }
                }

                10 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.privacy_policy2))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.PRIVACY_POLICY.contentType)
                    }
                }

                11 -> {
                    (mActivity as HomeActivity).startActivity<WebViewActivity> {
                        putExtra(AppConstants.EXTRA_WEBVIEW_TITLE, mActivity.getString(R.string.terms_n_condition))
                        putExtra(AppConstants.EXTRA_WEBVIEW_URL, ContentType.TERMS_N_CONDITION.contentType)
                    }
                }

                12 -> {
                    (mActivity as HomeActivity).logoutDialog(object : BaseActivity.LogoutOkListener{
                        override fun onOk() {
                            Log.e("Logout", "Logout Clicked")
                            val params = HashMap<String, String>()
                            logoutObserver(params)
                        }
                    })
                }
            }
        }
    }

    private fun getCartCount() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getCartCount()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun cartCountObserver() {

        getCartCount().observe(mBinding.lifecycleOwner!!) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            AppPreferencesHelper.getInstance().cartItemCount =
                                users.data?.cartCount!!
                            AppPreferencesHelper.getInstance().notificationCount =
                                users.data?.notificationCount!!
                            CommonUtils.showCartItemCount(mBinding.toolbar.bottom_navigation_notification)
                            CommonUtils.showNotificationCount(mBinding.toolbar.txtNotificationCount)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        }
    }



    fun logout(params: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.logout(params)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun logoutObserver(params: HashMap<String, String>) {
        logout(params).observe(mBinding.lifecycleOwner!!) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            AppPreferencesHelper.getInstance().clearAllPreferences()
                            val intent = Intent(mActivity, LoginActivity::class.java)
                            intent.putExtra("isFromOtherScreen", true)
                            mActivity.startActivityForResult(intent, AppRequestCode.REQUEST_LOGIN)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        }
    }
}