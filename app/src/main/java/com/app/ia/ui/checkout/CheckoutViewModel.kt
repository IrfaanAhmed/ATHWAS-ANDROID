package com.app.ia.ui.checkout

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.apiclient.Api
import com.app.ia.apiclient.RetrofitFactory
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
import com.ccavenue.indiasdk.AvenueOrder
import com.ccavenue.indiasdk.AvenuesApplication
import kotlinx.coroutines.Dispatchers
import java.math.BigInteger
import java.security.MessageDigest

class CheckoutViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var checkoutActivity: CheckoutActivity
    lateinit var mBinding: ActivityCheckoutBinding

    var address = MutableLiveData("")
    var addressType = MutableLiveData("")
    var addressId = MutableLiveData("")
    var isDeliverableArea = MutableLiveData(true)

    val isItemAvailable = MutableLiveData(true)
    val slidingText = MutableLiveData("Swipe to place order")
    var isCartChanged = false
    var selectedPaymentMethod = "Cash"

    var orderIdForOnlinePayment = MutableLiveData("")
    var paymentStatusResponse = MutableLiveData<PaymentStatusResponse>()

    var cartList = MutableLiveData<MutableList<CartListResponse.Docs>>()
    private val cartListAll = ArrayList<CartListResponse.Docs>()
    val promoCodeResponse = MutableLiveData<PromoCodeResponse>()

    private val totalItems = MutableLiveData("0.00")
    val totalAmount = MutableLiveData("0.00")
    val netAmount = MutableLiveData("0.00")
    val deliveryCharges = MutableLiveData("0.00")
    private var walletAmount = MutableLiveData("0.00")
    var vatAmount = MutableLiveData("0.00")
    var warehouseId = MutableLiveData("")
    var redeemPoint = MutableLiveData("0.00")

    val totalAmountWithoutOfferPrice = MutableLiveData("0.00")
    val totalAmountWithOfferPrice = MutableLiveData("0.00")

    var addedRedeemPointToDiscount = MutableLiveData("0.00")

    var encVal = MutableLiveData("")
    var vResponse = MutableLiveData("")

    fun setVariable(mBinding: ActivityCheckoutBinding, checkoutActivity: CheckoutActivity) {
        this.checkoutActivity = checkoutActivity
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.checkout))

        promoCodeResponse.value = PromoCodeResponse()
        walletAmount.value = AppPreferencesHelper.getInstance().walletAmount

        mBinding.edtTextName.filters = arrayOf(CommonUtils.getLettersEditTextFilter())

        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = "1"
        requestParams["limit"] = "400"
        cartListingObserver(requestParams)

        mBinding.rewardCheckBox.setOnClickListener {
            setNetAmount()
        }

        mBinding.rewardPointsLayout.setOnClickListener {
            if(mBinding.rewardCheckBox.isEnabled){
                mBinding.rewardCheckBox.isChecked = !mBinding.rewardCheckBox.isChecked
                setNetAmount()
            }
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
        return addedRedeemPointToDiscount.value!!.toString()
    }

    fun onRemovePromoCode() {
        promoCodeResponse.value = PromoCodeResponse()
        setNetAmount()
    }

    private fun setNetAmount() {
        if (mBinding.rewardCheckBox.isChecked) {
            netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!!.toDouble() + deliveryCharges.value!!.toDouble() + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble())

            if (redeemPoint.value!! > netAmount.value!!) {
                addedRedeemPointToDiscount.value = netAmount.value
                netAmount.value = "0.00"
            } else {
                netAmount.value = CommonUtils.convertToDecimal(netAmount.value!!.toDouble() - redeemPoint.value!!.toDouble())
                addedRedeemPointToDiscount.value = redeemPoint.value
            }

            if (netAmount.value!!.toDouble() < 0) {
                netAmount.value = "0.00"
            }
        } else {
            netAmount.value = CommonUtils.convertToDecimal(totalAmount.value!!.toDouble() + deliveryCharges.value!!.toDouble() + vatAmount.value!!.toDouble() - promoCodeResponse.value?.discountPrice!!.toDouble())
            addedRedeemPointToDiscount.value = "0.00"
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
                            totalItems.value = "0.00"
                            totalAmount.value = "0.00"
                            //Newly added
                            totalAmountWithOfferPrice.value = "0.00"
                            totalAmountWithoutOfferPrice.value = "0.00"

                            for (cartItem in cartListAll) {
                                totalItems.value = totalItems.value!! + cartItem.categoryItems.size
                                for (item in cartItem.categoryItems) {
                                    totalAmountWithoutOfferPrice.value = CommonUtils.convertToDecimal(totalAmountWithoutOfferPrice.value!!.toDouble() + (item.getPrice().toDouble() * item.quantity))
                                    if (item.isDiscount == 1) {
                                        totalAmountWithOfferPrice.value = CommonUtils.convertToDecimal(totalAmountWithOfferPrice.value!!.toDouble() + (item.getPrice().toDouble() - item.getOfferPrice().toDouble()) * item.quantity)
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!!.toDouble() + (item.getOfferPrice().toDouble() * item.quantity))
                                    } else {
                                        totalAmount.value = CommonUtils.convertToDecimal(totalAmount.value!!.toDouble() + (item.getPrice().toDouble() * item.quantity))
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

                            val selectedAddress = AppPreferencesHelper.getInstance().defaultAddress
                            if (selectedAddress.Id.isNotEmpty() && selectedAddress.Id != null) {
                                addressType.value = selectedAddress.addressType
                                address.value = selectedAddress.fullAddress
                                addressId.value = selectedAddress.Id
                                val addressParams = HashMap<String, String>()
                                addressParams["delivery_address_id"] = addressId.value!!
                                addressParams["order_amount"] = totalAmount.value.toString()
                                getDeliveryFeeObserver(addressParams)
                            }

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
                            request["limit"] = "400"
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
                                val dialog = IADialog(mActivity, "", users.message, true)
                                dialog.setOnItemClickListener(object: IADialog.OnClickListener{
                                    override fun onPositiveClick() {
                                        mActivity.startActivityWithFinish<HomeActivity> {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                    }
                                    override fun onNegativeClick() {

                                    }
                                })

                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                        (mActivity as CheckoutActivity).changeSwipeButtonStatus()

                        isCartChanged = true
                        cartListAll.clear()
                        val request = HashMap<String, String>()
                        request["page_no"] = "1"
                        request["limit"] = "400"
                        cartListingObserver(request)
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
                                deliveryCharges.value = CommonUtils.convertToDecimal(users.data?.deliveryFee!!)
                            }
                            walletAmount.value = CommonUtils.convertToDecimal(users.data?.wallet!!)
                            warehouseId.value = users.data?.warehouse!!
                            redeemPoint.value = CommonUtils.convertToDecimal(users.data?.getRedeemPoint()!!)
                            if (users.data?.vatAmount == "null" || users.data?.vatAmount == null) {
                                vatAmount.value = "0.00"
                            } else {
                                vatAmount.value = CommonUtils.convertToDecimal(users.data?.vatAmount)
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

                            val str = "${orderIdForOnlinePayment.value}INR${netAmount.value!!.toString()}AVTL07ID25BH87LTHB376194"
                            Log.d("Payment", "Sha512 Str:  $str")
                            val requestHash = getSHA512(str)
                            Log.d("Payment", "Sha512 $requestHash")

                           /* val orderDetails= AvenueOrder()
                            orderDetails.setOrderId(orderIdForOnlinePayment.value)
                            orderDetails.setResponseHash(requestHash);
                            orderDetails.setRsaKeyUrl(ServiceUtility.chkNull(Api.RSA_URL));
                            orderDetails.setRedirectUrl(ServiceUtility.chkNull(Api.REDIRECT_URL));
                            orderDetails.setCancelUrl(ServiceUtility.chkNull(Api.CANCEL_URL));
                            orderDetails.setAccessCode(ServiceUtility.chkNull("AVTL07ID25BH87LTHB"));
                            orderDetails.setMerchantId(ServiceUtility.chkNull("376194"));
                            orderDetails.setCurrency(ServiceUtility.chkNull("INR"));
                            orderDetails.setAmount(ServiceUtility.chkNull(netAmount.value!!.toString()));
                            orderDetails.setCustomerId(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().userID));
                            orderDetails.setPaymentType("[creditcard,debitcard,netbanking,wallet,upi]");
                            orderDetails.setMerchantLogo("login_logo.png");
                            orderDetails.setBillingName("Manoj");
                            orderDetails.setBillingAddress("Test");
                            orderDetails.setBillingCountry("India");
                            orderDetails.setBillingState("Rajasthan");
                            orderDetails.setBillingCity("Jaipur");
                            orderDetails.setBillingZip("302019");
                            orderDetails.setBillingTel("9549063358");
                            orderDetails.setBillingEmail(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().email));
                            orderDetails.setDeliveryName(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().userName));
                            orderDetails.setDeliveryAddress("Jaipur");
                            orderDetails.setDeliveryCountry("India");
                            orderDetails.setDeliveryState("Rajasthan");
                            orderDetails.setDeliveryCity("Jaipur");
                            orderDetails.setDeliveryZip("302019");
                            orderDetails.setDeliveryTel("9549063358");
                            orderDetails.setMerchant_param1("test"); //total 5 parameters
                            orderDetails.setMobileNo("9549063358")
                            orderDetails.setPaymentEnviroment("app_staging")

                            AvenuesApplication.startTransaction(checkoutActivity, orderDetails);
                            Log.d("Payment", "Init")*/

                            val params = java.util.HashMap<String, String>()
                            params["accessCode"] = ServiceUtility.chkNull("AVTL07ID25BH87LTHB")
                            params["requestId"] = orderIdForOnlinePayment.value!!
                            params["requestHash"] = ServiceUtility.chkNull("$requestHash")
                            //getRSAKeyObservers(params)

                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                        (mActivity as CheckoutActivity).changeSwipeButtonStatus()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getRSAKey(requestParams: java.util.HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = RetrofitFactory.getPaymentInstance().getRSAKey(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getRSAKeyObservers(requestParams: java.util.HashMap<String, String>) {

        getRSAKey(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            vResponse.value = users.body()!!.string()
                            Log.d("Payment", "KeyResp: ${vResponse.value}")
                            val vEncVal = StringBuffer()
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, ServiceUtility.chkNull(netAmount.value!!.toString())))
                            vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR")))
                            encVal.value = getSHA512(vResponse.value!!)//RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length - 1), vResponse.value!!)
                            //callPaymentURl()

                            val orderDetails= AvenueOrder()
                            orderDetails.setOrderId(orderIdForOnlinePayment.value)
                            orderDetails.setResponseHash(encVal.value);
                            orderDetails.setRsaKeyUrl(ServiceUtility.chkNull(Api.RSA_URL));
                            orderDetails.setRedirectUrl(ServiceUtility.chkNull(Api.REDIRECT_URL));
                            orderDetails.setCancelUrl(ServiceUtility.chkNull(Api.CANCEL_URL));
                            orderDetails.setAccessCode(ServiceUtility.chkNull("AVTL07ID25BH87LTHB"));
                            orderDetails.setMerchantId(ServiceUtility.chkNull("376194"));
                            orderDetails.setCurrency(ServiceUtility.chkNull("INR"));
                            orderDetails.setAmount(ServiceUtility.chkNull(netAmount.value!!.toString()));
                            orderDetails.setCustomerId(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().userID));
                            orderDetails.setPaymentType("[creditcard,debitcard,netbanking,wallet,upi]");
                            orderDetails.setMerchantLogo("login_logo.png");
                            orderDetails.setBillingName("Manoj");
                            orderDetails.setBillingAddress("Test");
                            orderDetails.setBillingCountry("India");
                            orderDetails.setBillingState("Rajasthan");
                            orderDetails.setBillingCity("Jaipur");
                            orderDetails.setBillingZip("302019");
                            orderDetails.setBillingTel("9549063358");
                            orderDetails.setBillingEmail(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().email));
                            orderDetails.setDeliveryName(ServiceUtility.chkNull(AppPreferencesHelper.getInstance().userName));
                            orderDetails.setDeliveryAddress("Jaipur");
                            orderDetails.setDeliveryCountry("India");
                            orderDetails.setDeliveryState("Rajasthan");
                            orderDetails.setDeliveryCity("Jaipur");
                            orderDetails.setDeliveryZip("302019");
                            orderDetails.setDeliveryTel("9549063358");
                            orderDetails.setMerchant_param1("test"); //total 5 parameters
                            orderDetails.setMobileNo("9549063358")
                            orderDetails.setPaymentEnviroment("app_staging")

                            AvenuesApplication.startTransaction(checkoutActivity, orderDetails);
                            Log.d("Payment", "Init")
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

    fun getSHA512(input:String):String{
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(input.toByteArray())

        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)

        // Convert message digest into hex value
        var hashtext: String = no.toString(16)

        // Add preceding 0s to make it 128 chars long
        while (hashtext.length < 128) {
            hashtext = "0$hashtext"
        }



        // return the HashText
        return hashtext
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
                            mActivity.toast(it.message)
                        }
                        (mActivity as CheckoutActivity).changeSwipeButtonStatus()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
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