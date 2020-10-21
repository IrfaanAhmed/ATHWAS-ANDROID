package com.app.ia.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityHomeBinding
import com.app.ia.helper.CardDrawerLayout
import com.app.ia.model.AddressListResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.CommonUtils.getAddressFromLocation
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    private var mActivityBinding: ActivityHomeBinding? = null
    private var mViewModel: HomeViewModel? = null

    private var mDrawer: CardDrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var latitude = 0.0
    private var longitude = 0.0

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
        //drawer_layout.setRadius(Gravity.START, 35.0f)
        drawer_layout.setViewElevation(Gravity.START, 20.0f)

        replaceFragment(HomeFragment.newInstance())
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(HomeViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString())
        /*if (fragment !is HomeFragment) {
            fragmentTransaction.addToBackStack(fragment.toString())
        }*/
        fragmentTransaction.commit()
    }

    fun setupHeader(title: String, isHomeScreen: Boolean) {
        mViewModel?.isHomeScreen?.set(isHomeScreen)
        mViewModel?.title?.set(title)
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
           if(data != null){
               var selectedAddress = data.getSerializableExtra(AppConstants.EXTRA_SELECTED_ADDRESS) as AddressListResponse.AddressList
               mViewModel?.addressTitle!!.value = selectedAddress.addressType
               mViewModel?.address!!.value = selectedAddress.fullAddress
           }
        }
    }

}