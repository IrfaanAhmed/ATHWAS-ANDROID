package com.app.ia.ui.checkout

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.apiclient.Api
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityCheckoutBinding
import com.app.ia.dialog.IADialog
import com.app.ia.dialog.bottom_sheet_dialog.PaymentSelectDialogFragment
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.CartListResponse
import com.app.ia.model.PaymentStatusResponse
import com.app.ia.model.PromoCodeResponse
import com.app.ia.ui.home.HomeActivity
import com.app.ia.ui.payment.AvenuesParams
import com.app.ia.ui.payment.PaymentActivity
import com.app.ia.ui.payment.StatusActivity
import com.app.ia.utils.*
import kotlinx.coroutines.Dispatchers

class CheckoutViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityCheckoutBinding

    var address = MutableLiveData("")
    var addressType = MutableLiveData("")
    var addressId = MutableLiveData("")
    var isDeliverableArea = MutableLiveData(true)

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")
    var isCartChanged = false
    var selectedPaymentMethod = "Cash"

    var orderIdForOnlinePayment = MutableLiveData("")
    var paymentStatusResponse = MutableLiveData<PaymentStatusResponse>()

    var cartList = MutableLiveData<MutableList<CartListResponse.Docs>>()
    private val cartListAll = ArrayList<CartListResponse.Docs>()
    val promoCodeResponse = MutableLiveData<PromoCodeResponse>()

    private val totalItems = MutableLiveData(0)
    val totalAmount = MutableLiveData(0.0)
    val netAmount = MutableLiveData(0.0)
    val deliveryCharges = MutableLiveData(0.0)
    private var walletAmount = MutableLiveData("0.0")
    var vatAmount = MutableLiveData("0.0")
    var warehouseId = MutableLiveData("")
    var redeemPoint = MutableLiveData(0.0)

    val totalAmountWithoutOfferPrice = MutableLiveData(0.0)
    val totalAmountWithOfferPrice = MutableLiveData(0.0)

    var addedRedeemPointToDiscount = MutableLiveData(0.0)

    fun setVariable(mBinding: ActivityCheckoutBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.checkout))

        promoCodeResponse.value = PromoCodeResponse()
        walletAmount.value = AppPreferencesHelper.getInstance().walletAmount

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = "1"
        requestParams["limit"] = "30"
        cartListingObserver(requestParams)

        mBinding.rewardCheckBox.setOnClickListener {
            setNetAmount()
        }

        mBinding.rewardPointsLayout.setOnClickListener {
            mBinding.rewardCheckBox.isChecked = !mBinding.rewardCheckBox.isChecked
            setNetAmount()
        }
    }

    fun onApplyPromoCode() {

        val enteredPromoCode = mBinding.edtTextPromoCode.text.toString()
        if (enteredPromoCode.isEmpty()) {
            IADialog(mActivity, "Please enter promo code.", true)
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["promo_code"] = enteredPromoCode
            requestParams["order_amount"] = totalAmount.value.toString()
            applyCouponObserver(requestParams)
        }
    }

    fun applicableRewardPoint(): String {
        return if (netAmount.value!! >= redeemPoint.value!!) {
            redeemPoint.value.toString()
        } else {
            (redeemPoint.value!! - netAmount.value!!).toString()
        }
    }

    fun onRemovePromoCode() {
        promoCodeResponse.value = PromoCodeResponse()
        setNetAmount()
    }

    fun setNetAmount() {
        if (mBinding.rewardCheckBox.isChecked) {
            netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + deliveryCharges.value!! + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble() - redeemPoint.value!!).toDouble()
            addedRedeemPointToDiscount.value = redeemPoint.value
            if (netAmount.value!! < 0) {
                netAmount.value = 0.0
            }
        } else {
            netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + deliveryCharges.value!! + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble()).toDouble()
            addedRedeemPointToDiscount.value = 0.0
        }
    }

    fun onPaymentMethodChange() {
        val bottomSheetFragment = PaymentSelectDialogFragment.newInstance(selectedPaymentMethod, walletAmount.value!!)
        bottomSheetFragment.setOnItemClickListener(object : PaymentSelectDialogFragment.OnWalletFilterClickListener {
            override fun onSubmitClick(selectedMethod: String) {

                selectedPaymentMethod = selectedMethod

                when (selectedPaymentMethod) {
                    "Cash" -> {
                        mBinding.txtPaymentType.text = "Pay on Delivery"
                    }
                    "Wallet" -> {
                        mBinding.txtPaymentType.text = "Athwas Pay"
                    }
                    "Credit" -> {
                        mBinding.txtPaymentType.text = "Credit/Debit Card/UPI"
                    }
                }
            }
        })
        bottomSheetFragment.show((mActivity as CheckoutActivity).supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun getCartListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getCartListing(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun cartListingObserver(requestParams: HashMap<String, String>) {

        getCartListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            cartListAll.addAll(users.data?.docs!!)
                            cartList.value = cartListAll
                            totalItems.value = 0
                            totalAmount.value = 0.0
                            //Newly added
                            totalAmountWithOfferPrice.value = 0.0
                            totalAmountWithoutOfferPrice.value = 0.0

                            for (cartItem in cartListAll) {
                                totalItems.value = totalItems.value!! + cartItem.categoryItems.size
                                for (item in cartItem.categoryItems) {
                                    totalAmountWithoutOfferPrice.value = CommonUtils.convertToDecimal(totalAmountWithoutOfferPrice.value!! + (item.getPrice().toDouble() * item.quantity)).toDouble()
                                    if (item.isDiscount == 1) {
                                        totalAmountWithOfferPrice.value = CommonUtils.convertToDecimal(totalAmountWithOfferPrice.value!! + (item.getPrice().toDouble() - item.getOfferPrice().toDouble()) * item.quantity).toDouble()
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + (item.getOfferPrice().toDouble() * item.quantity)).toDouble()
                                    } else {
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + (item.getPrice().toDouble() * item.quantity)).toDouble()
                                    }
                                }
                            }
                            setNetAmount()
                            //netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + deliveryCharges.value!! + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble()).toDouble()

                            if (cartListAll.size <= 0) {
                                isCartChanged = true
                                val intent = Intent()
                                intent.putExtra("cartChanged", isCartChanged)
                                mActivity.setResult(Activity.RESULT_OK, intent)
                                mActivity.finish()
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
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
                            isCartChanged = true
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
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun placeOrder(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.placeOrder(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun placeOrderObserver(requestParams: HashMap<String, String>) {

        placeOrder(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->

                            if (selectedPaymentMethod == "Credit") {
                                mActivity.startActivityWithFinish<StatusActivity> {
                                    putExtra("status", paymentStatusResponse.value)
                                }
                            } else {
                                Toast.makeText(mActivity, users.message, Toast.LENGTH_LONG).show()
                                mActivity.startActivityWithFinish<HomeActivity> {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getDeliveryFee(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getDeliveryFee(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDeliveryFeeObserver(requestParams: HashMap<String, String>) {

        getDeliveryFee(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->

                            isDeliverableArea.value = true
                            if (users.data?.deliveryFee!!.isNotEmpty()) {
                                deliveryCharges.value = CommonUtils.convertToDecimal(users.data?.deliveryFee!!).toDouble()
                            }
                            walletAmount.value = users.data?.wallet!!
                            warehouseId.value = users.data?.warehouse!!
                            redeemPoint.value = users.data?.getRedeemPoint()!!
                            if (users.data?.vatAmount == "null" || users.data?.vatAmount == null) {
                                vatAmount.value = "0.0"
                            } else {
                                vatAmount.value = users.data?.vatAmount
                            }
                            AppPreferencesHelper.getInstance().walletAmount = walletAmount.value!!
                            setNetAmount()
                            //netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + deliveryCharges.value!! + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble()).toDouble()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            isDeliverableArea.value = false
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /**
     *
     */
    private fun applyCoupon(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.applyCoupon(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun applyCouponObserver(requestParams: HashMap<String, String>) {

        applyCoupon(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            promoCodeResponse.value = users.data!!
                            setNetAmount()
                            //netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!! + deliveryCharges.value!!.toDouble() + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble()).toDouble()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /*
    * Generate Order ID for online payments
    * */
    /**
     *
     */
    private fun generateOrderId(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.generateOrderID(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun generateOrderIdObserver(requestParams: HashMap<String, String>) {

        generateOrderId(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->

                            //{"data":{"_id":"6093e0fa7ff47a459789ff2f","order_id":21613076,"payment_mode":"Online"}}
                            orderIdForOnlinePayment.value = users.data?.orderId!!

                            val intent = Intent(mActivity, PaymentActivity::class.java)
                            // test
//                            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull("AVTL07ID25BH87LTHB"))
//                            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull("376194"))
                            //live
                            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull("AVTL07ID25BH87LTHB"))
                            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull("376194"))

                            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderIdForOnlinePayment.value))
                            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR"))
                            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(netAmount.value!!.toString()))
                            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(Api.REDIRECT_URL))
                            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(Api.CANCEL_URL))
                            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(Api.RSA_URL))
                            intent.putExtra(AvenuesParams.PAYMENT_FOR, "Order")
                            mActivity.startActivityForResult(intent, AppRequestCode.REQUEST_PAYMENT_CODE)
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /*
    *  Check Payment Status
    * */
    private fun checkPaymentStatus(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.checkPaymentStatus(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun checkPaymentStatusObserver(requestParams: HashMap<String, String>) {

        checkPaymentStatus(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            paymentStatusResponse.value = users.data!!

                            if (paymentStatusResponse.value!!.requestType == 2) {
                                (mActivity as CheckoutActivity).placeOrder()
                            } else {
                                mActivity.startActivity<StatusActivity> {
                                    putExtra("status", paymentStatusResponse.value)
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}