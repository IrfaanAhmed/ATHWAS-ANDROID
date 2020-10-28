package com.app.ia.ui.my_profile

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProfileBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.ProfileResponse
import com.app.ia.ui.delivery_address.DeliveryAddressActivity
import com.app.ia.ui.edit_profile.EditProfileActivity
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.rewards.RewardsActivity
import com.app.ia.ui.wishlist.WishListActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.AppConstants.EXTRA_PROFILE_DETAIL
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivity
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.Dispatchers

class ProfileViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    private lateinit var mActivity: Activity
    private lateinit var mBinding: ActivityProfileBinding

    var userData = MutableLiveData<ProfileResponse>()

    fun setVariable(mBinding: ActivityProfileBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.profile))
        mBinding.toolbar.ivEditProfileIcon.setOnClickListener { onEditProfileClick() }
        setupObservers()
    }

    fun onRewardPointClick() {
        mActivity.startActivity<RewardsActivity>()
    }

    fun onWishListClick() {
        mActivity.startActivity<WishListActivity>()
    }

    fun onDeliveryAddressClick() {
        mActivity.startActivity<DeliveryAddressActivity> {
            putExtra(AppConstants.EXTRA_IS_HOME_SCREEN, false)
        }
    }

    private fun onEditProfileClick() {
        mActivity.startActivity<EditProfileActivity> {
            putExtra(EXTRA_PROFILE_DETAIL, userData.value)
        }
    }

    fun onLogOutClick() {
        (mActivity as ProfileActivity).logoutDialog()
    }

    private fun getProfile() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProfile()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers() {
        getProfile().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {
                                userData.value = users.data
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

}