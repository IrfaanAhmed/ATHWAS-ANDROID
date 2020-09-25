package com.app.ia.ui.home

import android.app.Activity
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityHomeBinding
import com.app.ia.ui.business_category.CategoryFragment
import com.app.ia.ui.home.adapter.NavigationListAdapter
import com.app.ia.ui.my_order.MyOrdersFragment
import com.app.ia.ui.my_profile.ProfileActivity
import com.app.ia.ui.offers.OffersFragment
import com.app.ia.ui.wallet.WalletFragment
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.nav_side_menu.*

class HomeViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), NavigationListAdapter.OnSideMenuItemClickListener {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityHomeBinding

    fun setVariable(mBinding: ActivityHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!

        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        (mActivity as HomeActivity).recViewNavigation.addItemDecoration(DividerItemDecoration(mActivity, LinearLayout.VERTICAL))
        val navigationAdapter = NavigationListAdapter()
        navigationAdapter.setOnSideMenuClickListener(this)
        (mActivity as HomeActivity).recViewNavigation.adapter = navigationAdapter
        val menuList = ArrayList<String>()
        menuList.add(mActivity.getString(R.string.home))
        menuList.add(mActivity.getString(R.string.shop_by_category))
        menuList.add(mActivity.getString(R.string.offers))
        menuList.add(mActivity.getString(R.string.my_account))
        menuList.add(mActivity.getString(R.string.my_orders))
        menuList.add(mActivity.getString(R.string.my_wallet))
        menuList.add(mActivity.getString(R.string.faq))
        menuList.add(mActivity.getString(R.string.about_us))
        menuList.add(mActivity.getString(R.string.contact_us))
        menuList.add(mActivity.getString(R.string.logout))
        navigationAdapter.submitList(menuList)
    }

    fun onHumBurgerMenuClick() {
        mBinding.drawerLayout.openDrawer(mBinding.navView)
    }

    override fun onSideMenuClick(position: Int) {

        if (mBinding.drawerLayout.isOpen) {
            mBinding.drawerLayout.closeDrawer(mBinding.navView)
        }

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
            }
        }
    }


}