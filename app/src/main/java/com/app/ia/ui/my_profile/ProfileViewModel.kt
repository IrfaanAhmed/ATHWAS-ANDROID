package com.app.ia.ui.my_profile

import android.app.Activity
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProfileBinding
import com.app.ia.ui.delivery_address.DeliveryAddressActivity
import com.app.ia.ui.edit_profile.EditProfileActivity
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.common_header.view.*

class ProfileViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityProfileBinding

    fun setVariable(mBinding: ActivityProfileBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.profile))

        mBinding.toolbar.ivEditProfileIcon.setOnClickListener { onEditProfileClick() }
    }

    fun onRewardPointClick(){
        //mActivity.startActivity<WalletActivity>()
    }

    fun onDeliveryAddressClick(){
        mActivity.startActivity<DeliveryAddressActivity>()
    }

    fun onEditProfileClick(){
        mActivity.startActivity<EditProfileActivity>()
    }

}