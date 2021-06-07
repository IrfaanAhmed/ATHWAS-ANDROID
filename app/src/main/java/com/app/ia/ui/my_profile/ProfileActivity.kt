package com.app.ia.ui.my_profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProfileBinding
import com.app.ia.utils.AppConstants
import com.app.ia.utils.setOnApplyWindowInset1
import com.app.ia.utils.visible
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.common_header.view.*

class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>() {

    private var mActivityBinding: ActivityProfileBinding? = null
    private var mViewModel: ProfileViewModel? = null

    private val updateProfileListReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mViewModel?.setupObservers()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile
    }

    override fun getViewModel(): ProfileViewModel {
        return mViewModel!!
    }

    override fun onDestroy() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@ProfileActivity)
        localBroadcastReceiver.unregisterReceiver(updateProfileListReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.ivEditProfileIcon.visible()

        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this@ProfileActivity)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_BROADCAST_UPDATE_PROFILE)
        localBroadcastReceiver.registerReceiver(updateProfileListReceiver, intentFilter)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProfileViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
    }
}