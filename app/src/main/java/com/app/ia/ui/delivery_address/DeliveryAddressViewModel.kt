package com.app.ia.ui.delivery_address

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityDeliveryAddressBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.AddressListResponse
import com.app.ia.ui.add_new_address.AddAddressActivity
import com.app.ia.ui.place_picker.PlacePickerActivity
import com.app.ia.utils.*
import com.app.ia.utils.AppConstants.EXTRA_IS_HOME_SCREEN
import kotlinx.coroutines.Dispatchers

class DeliveryAddressViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityDeliveryAddressBinding

    val isItemAvailable = MutableLiveData(true)
    var isFromHomeScreen = MutableLiveData(false)

    private val addressList = ArrayList<AddressListResponse.AddressList>()
    val addressListResponse = MutableLiveData<MutableList<AddressListResponse.AddressList>>()
    var deletedPosition = MutableLiveData(-1)
    var previousSelectAddressId = MutableLiveData("")
    var deletedAddressIds = ArrayList<String>()

    fun setVariable(mBinding: ActivityDeliveryAddressBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.my_address))
        if (AppPreferencesHelper.getInstance().authToken.isNotEmpty()) {
            getAddressesObserver(HashMap())
        }
    }

    fun setIntent(intent: Intent) {
        isFromHomeScreen.value = intent.getBooleanExtra(EXTRA_IS_HOME_SCREEN, false)
        if(intent.getStringExtra("selectAddressId") != null) {
            previousSelectAddressId.value = intent.getStringExtra("selectAddressId")
        }
    }

    fun onAddAddressClick() {
        if (AppPreferencesHelper.getInstance().authToken.isNotEmpty()) {
            mActivity.mStartActivityForResult<AddAddressActivity>(AppRequestCode.REQUEST_ADD_ADDRESS) {
                putExtra("addressCount", addressList.size)
            }
        } else {
            (mActivity as DeliveryAddressActivity).loginDialog()
        }
    }

    private fun getAddresses(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getAddresses()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAddressesObserver(requestParams: HashMap<String, String>) {
        addressList.clear()
        getAddresses(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            addressList.addAll(users.data?.addresslist!!)
                            addressListResponse.value = addressList

                            var isDefaultAddressExist = false
                            for (defaultAddress in addressList) {
                                if (defaultAddress.defaultAddress == 1) {
                                    isDefaultAddressExist = true
                                    AppPreferencesHelper.getInstance().defaultAddress = defaultAddress
                                }
                            }

                            if (!isDefaultAddressExist) {
                                AppPreferencesHelper.getInstance().defaultAddress = AddressListResponse.AddressList()
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun deleteAddresses(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.deleteAddress(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteAddressesObserver(requestParams: HashMap<String, String>) {
        deleteAddresses(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)

                            val addressId = addressList[deletedPosition.value!!].Id
                            if (addressList[deletedPosition.value!!].defaultAddress == 1) {
                                AppPreferencesHelper.getInstance().defaultAddress = AddressListResponse.AddressList()
                            }
                            deletedAddressIds.add(addressId)
                            addressList.removeAt(deletedPosition.value!!)
                            addressListResponse.value = addressList
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun setDefaultAddress(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.setDefaultAddress(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setDefaultAddressObserver(requestParams: HashMap<String, String>) {
        setDefaultAddress(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { _ ->
                            //mActivity.toast(users.message)
                            getAddressesObserver(HashMap())
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}