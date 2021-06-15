package com.app.ia.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.Api
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityCheckoutBinding
import com.app.ia.dialog.IADialog
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.AddressListResponse
import com.app.ia.model.CartListResponse
import com.app.ia.ui.checkout.adapter.CheckoutAdapter
import com.app.ia.ui.delivery_address.DeliveryAddressActivity
import com.app.ia.ui.payment.AvenuesParams
import com.app.ia.ui.payment.PaymentActivity
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.common_header.view.*
import org.json.JSONArray

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding, CheckoutViewModel>(), CheckoutUpdateListener {

    private var mBinding: ActivityCheckoutBinding? = null
    private var mViewModel: CheckoutViewModel? = null

    var cartAdapter: CheckoutAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_checkout
    }

    override fun getViewModel(): CheckoutViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        //makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_container)) { _, insets ->
            //so on for left and right insets
            toolbar.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        toolbar.imageViewIcon.gone()
        toolbar.ivSearchIcon.gone()
        toolbar.ivEditProfileIcon.gone()

        deliveryAddressLayout.setOnClickListener {
            mStartActivityForResult<DeliveryAddressActivity>(AppRequestCode.REQUEST_SELECT_ADDRESS) {
                putExtra(AppConstants.EXTRA_IS_HOME_SCREEN, true)
            }
        }

        txtViewChange.setOnClickListener {
            mStartActivityForResult<DeliveryAddressActivity>(AppRequestCode.REQUEST_SELECT_ADDRESS) {
                putExtra(AppConstants.EXTRA_IS_HOME_SCREEN, true)
            }
        }


        cartAdapter = CheckoutAdapter(this)
        recViewCheckout.adapter = cartAdapter
        mViewModel?.cartList?.observe(this, {

            if (it.size <= 0) {
                cartAdapter?.notifyDataSetChanged()
            } else {
                if (cartAdapter?.currentList?.size!! == 0) {
                    cartAdapter?.submitList(it)
                } else {
                    cartAdapter?.notifyDataSetChanged()
                }
            }
        })

        buttonPlaceOrder.setOnClickListener {
            if (mViewModel?.addressId?.value!!.isEmpty()) {
                Toast.makeText(this@CheckoutActivity, "Please select Address", Toast.LENGTH_LONG).show()
            } else if (!mViewModel?.isDeliverableArea?.value!!) {
                Toast.makeText(this@CheckoutActivity, "We don't deliver in the selected area. Please choose another address.", Toast.LENGTH_LONG).show()
            } else {

                if (mViewModel?.selectedPaymentMethod == "Wallet") {
                    val walletAmount = AppPreferencesHelper.getInstance().walletAmount.toDouble()
                    if (walletAmount < mViewModel?.netAmount?.value!!) {
                        IADialog(this@CheckoutActivity, "Wallet amount is low. Please add amount.", true)
                        return@setOnClickListener
                    }
                }

                if (mViewModel?.selectedPaymentMethod == "Credit") {
                    val requestParams = HashMap<String, String>()
                    requestParams["payment_mode"] = "Online"
                    requestParams["delivery_address"] = mViewModel?.addressId?.value!!
                    mViewModel?.generateOrderIdObserver(requestParams)
                } else {
                    placeOrder()
                }
            }
        }
    }

    fun placeOrder() {
        val requestParams = HashMap<String, String>()
        requestParams["payment_mode"] = mViewModel?.selectedPaymentMethod!!

        if(mViewModel?.orderIdForOnlinePayment?.value!!.isNotEmpty()) {
            requestParams["order_id"] = mViewModel?.orderIdForOnlinePayment?.value!!
        }

        if (mViewModel?.promoCodeResponse?.value?.promoCode == null) {
            requestParams["promo_code"] = ""
            requestParams["promo_code_id"] = ""
            requestParams["discount_price"] = "0"
        } else {
            requestParams["promo_code"] = mViewModel?.promoCodeResponse?.value?.promoCode!!
            requestParams["promo_code_id"] = mViewModel?.promoCodeResponse?.value?.id!!
            requestParams["discount_price"] = mViewModel?.promoCodeResponse?.value?.discountPrice!!
        }
        requestParams["delivery_address"] = mViewModel?.addressId?.value!!
        requestParams["warehouse_id"] = mViewModel?.warehouseId?.value!!
        requestParams["delivery_fee"] = mViewModel?.deliveryCharges?.value!!.toString()
        requestParams["vat_amount"] = mViewModel?.vatAmount?.value!!
        requestParams["offers"] = JSONArray().toString()
        mViewModel?.placeOrderObserver(requestParams)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(CheckoutViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(CheckoutViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_SELECT_ADDRESS && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedAddress = data.getSerializableExtra(AppConstants.EXTRA_SELECTED_ADDRESS) as AddressListResponse.AddressList
                mViewModel?.addressType!!.value = selectedAddress.addressType
                mViewModel?.address!!.value = selectedAddress.fullAddress
                mViewModel?.addressId?.value = selectedAddress.Id
                val requestParams = HashMap<String, String>()
                requestParams["delivery_address_id"] = mViewModel?.addressId?.value!!
                requestParams["order_amount"] = mViewModel?.totalAmount?.value.toString()
                mViewModel?.getDeliveryFeeObserver(requestParams)
            }
        } else if (requestCode == AppRequestCode.REQUEST_PAYMENT_CODE && resultCode == RESULT_OK) {
            val requestParams = HashMap<String, String>()
            requestParams["order_id"] = mViewModel?.orderIdForOnlinePayment?.value!!
            requestParams["payment_for"] = "Order"
            mViewModel?.checkPaymentStatusObserver(requestParams)
        }
    }

    override fun onDeleteItem(productItem: CartListResponse.Docs.CategoryItems, position: Int) {
        val deleteDialog = IADialog(this@CheckoutActivity, "Do you want to delete this item?", false)
        deleteDialog.setOnItemClickListener(object : IADialog.OnClickListener {
            override fun onPositiveClick() {
                mViewModel?.deleteCartItemObserver(productItem.cartId)
            }

            override fun onNegativeClick() {
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("cartChanged", mViewModel?.isCartChanged)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}