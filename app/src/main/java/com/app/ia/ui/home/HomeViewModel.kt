package com.app.ia.ui.home

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityHomeBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.LoginResponse
import com.app.ia.model.ProfileResponse
import com.app.ia.ui.business_category.CategoryFragment
import com.app.ia.ui.delivery_address.DeliveryAddressActivity
import com.app.ia.ui.home.adapter.NavigationListAdapter
import com.app.ia.ui.login.LoginActivity
import com.app.ia.ui.my_order.MyOrdersFragment
import com.app.ia.ui.my_profile.ProfileActivity
import com.app.ia.ui.notification.NotificationActivity
import com.app.ia.ui.offers.OffersFragment
import com.app.ia.ui.wallet.WalletFragment
import com.app.ia.utils.AppConstants.EXTRA_IS_HOME_SCREEN
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.mStartActivityForResult
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.nav_side_menu.*

class HomeViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), NavigationListAdapter.OnSideMenuItemClickListener {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityHomeBinding
    val userData = MutableLiveData<LoginResponse>()

    var addressTitle = MutableLiveData<String>("Current Location")
    var address = MutableLiveData<String>("Select Address")

    fun setVariable(mBinding: ActivityHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        userData.value = AppPreferencesHelper.getInstance().userData

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
        if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            menuList.add(mActivity.getString(R.string.login))
        } else {
            menuList.add(mActivity.getString(R.string.logout))
        }
        navigationAdapter.submitList(menuList)
    }

    fun onHumBurgerMenuClick() {
        mBinding.drawerLayout.openDrawer(mBinding.navView)
    }

    fun onSelectAddressClick() {
        mActivity.mStartActivityForResult<DeliveryAddressActivity>(AppRequestCode.REQUEST_SELECT_ADDRESS) {
            putExtra(EXTRA_IS_HOME_SCREEN, true)
        }
    }

    fun onCartClick() {
        //mActivity.startActivity<MyCartActivity>()
    }

    fun onNotificationClick() {
        mActivity.startActivity<NotificationActivity>()
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
                    (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
                }
                3 -> {
                }
                4 -> {
                }
                5 -> {
                }
                6 -> {
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
                9 -> {
                    (mActivity as HomeActivity).logoutDialog()
                }
                /*6 -> {
                (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
            }
            7 -> {
                (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
            }
            8 -> {
                (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
            }
            9 -> {
                (mActivity as HomeActivity).replaceFragment(OffersFragment.newInstance())
            }*/
            }
        }
    }
}