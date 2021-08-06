package com.app.ia.ui.my_cart

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityMyCartBinding
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.CartListResponse
import com.app.ia.ui.home.HomeActivity
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivityWithFinish
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class MyCartViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityMyCartBinding

    val isItemAvailable = MutableLiveData(true)

    var cartList = MutableLiveData<MutableList<CartListResponse.Docs>>()
    val cartListAll = ArrayList<CartListResponse.Docs>()

    val totalItems = MutableLiveData(0)
    val totalAmount = MutableLiveData(0.0)
    private val totalAmountWithoutOffer = MutableLiveData(0.0)
    val totalSavedAmount = MutableLiveData(0.0)


    fun moveToHomePage() {
        mActivity.startActivityWithFinish<HomeActivity> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    fun setVariable(mBinding: ActivityMyCartBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.cart))

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = "1"
        requestParams["limit"] = "50"
        cartListingObserver(requestParams)
    }

    private fun getCartListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getCartListing(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun cartListingObserver(requestParams: HashMap<String, String>) {

        getCartListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            cartListAll.addAll(users.data?.docs!!)
                            cartList.value = cartListAll
                            isItemAvailable.value = cartListAll.size > 0
                            totalItems.value = 0
                            totalAmount.value = 0.0
                            totalAmountWithoutOffer.value = 0.0
                            for (cartItem in cartListAll) {
                                totalItems.value = totalItems.value!! + cartItem.categoryItems.size
                                for (item in cartItem.categoryItems) {
                                    totalAmountWithoutOffer.value = CommonUtils.convertToDecimal(totalAmountWithoutOffer.value!! + (item.getPrice().toDouble() * item.quantity)).toDouble()
                                    if (item.isDiscount == 1) {
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + (item.getOfferPrice().toDouble() * item.quantity)).toDouble()
                                    } else {
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + (item.getPrice().toDouble() * item.quantity)).toDouble()
                                    }
                                }
                            }



                            totalSavedAmount.value = totalAmountWithoutOffer.value!! - totalAmount.value!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


    private fun updateCartItem(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.updateCartItem(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun updateCartItemObserver(requestParams: HashMap<String, String>) {

        updateCartItem(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            cartListAll.clear()
                            val request = HashMap<String, String>()
                            request["page_no"] = "1"
                            request["limit"] = "30"
                            cartListingObserver(request)
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
        })
    }

    private fun deleteCartItem(cart_id: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.deleteCartItem(cart_id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteCartItemObserver(cart_id: String) {

        deleteCartItem(cart_id).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { _ ->
                            AppPreferencesHelper.getInstance().cartItemCount = it.data?.data?.cartCount!!
                            cartListAll.clear()
                            val request = HashMap<String, String>()
                            request["page_no"] = "1"
                            request["limit"] = "30"
                            cartListingObserver(request)
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
        })
    }

    fun isCartHaveNotAvailableProduct() : Boolean{
        var has = false
        cartList.value?.forEach {
            it.categoryItems?.forEach {
                if(it.isAvailable == 0 || it.availableQuantity <= 0){
                    has = true
                }
            }
        }
        return has
    }

}