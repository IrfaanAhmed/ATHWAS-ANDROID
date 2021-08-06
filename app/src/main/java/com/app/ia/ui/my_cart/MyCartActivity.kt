package com.app.ia.ui.my_cart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityMyCartBinding
import com.app.ia.dialog.IADialog
import com.app.ia.model.CartListResponse
import com.app.ia.ui.checkout.CheckoutActivity
import com.app.ia.ui.my_cart.adapter.CartListAdapter
import com.app.ia.utils.gone
import com.app.ia.utils.mStartActivityForResult
import com.app.ia.utils.setMarginTop
import com.app.ia.utils.toast
import kotlinx.android.synthetic.main.activity_my_cart.*
import kotlinx.android.synthetic.main.common_header.view.*

class MyCartActivity : BaseActivity<ActivityMyCartBinding, MyCartViewModel>(), CartUpdateListener {

    private var mBinding: ActivityMyCartBinding? = null
    private var mViewModel: MyCartViewModel? = null

    var cartAdapter: CartListAdapter? = null
    

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_cart
    }

    override fun getViewModel(): MyCartViewModel {
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

        cartAdapter = CartListAdapter(this)
        recViewCart.adapter = cartAdapter
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

        buttonCheckout.setOnClickListener {
            if(mViewModel?.cartList?.value?.size!! <= 0){
                toast("There is no item in the cart")
            }
            else if (mViewModel?.isCartHaveNotAvailableProduct()!!) {
                toast("Please remove out of stock & not available product")
            } else {
                mStartActivityForResult<CheckoutActivity>(2009)
            }

        }
    }



    private fun setViewModel() {
        val factory = ViewModelFactory(MyCartViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(MyCartViewModel::class.java)
    }

    override fun onUpdate(productItem: CartListResponse.Docs.CategoryItems, quantity: Int) {
        val requestParams = HashMap<String, String>()
        requestParams["inventory_id"] = productItem.inventoryId
        requestParams["product_id"] = productItem.productId
        requestParams["quantity"] = "" + quantity
        requestParams["business_category_id"] = productItem.businessCategory.Id
        mViewModel?.updateCartItemObserver(requestParams)
    }

    override fun onDeleteItem(productItem: CartListResponse.Docs.CategoryItems, position: Int) {
        //val requestParams = HashMap<String, String>()
        val deleteDialog = IADialog(this@MyCartActivity, "Do you want to delete this item?", false)
        deleteDialog.setOnItemClickListener(object : IADialog.OnClickListener {
            override fun onPositiveClick() {
                mViewModel?.deleteCartItemObserver(productItem.cartId)
            }

            override fun onNegativeClick() {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2009 && resultCode == RESULT_OK) {
            val isCartChanged = data?.getBooleanExtra("cartChanged", false)!!
            if(isCartChanged) {
                mViewModel?.cartListAll?.clear()
                val requestParams = HashMap<String, String>()
                requestParams["page_no"] = "1"
                requestParams["limit"] = "30"
                mViewModel?.cartListingObserver(requestParams)
            }
        }
    }
}