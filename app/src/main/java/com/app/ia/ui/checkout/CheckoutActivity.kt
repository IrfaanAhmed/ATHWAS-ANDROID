package com.app.ia.ui.checkout

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityCheckoutBinding
import com.app.ia.ui.checkout.adapter.CheckoutAdapter
import com.app.ia.ui.my_cart.adapter.CartListAdapter
import com.app.ia.utils.gone
import com.app.ia.utils.setMarginTop
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.common_header.view.*

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding, CheckoutViewModel>() {

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


        cartAdapter = CheckoutAdapter()
        recViewCheckout.adapter = cartAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        cartAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(CheckoutViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(CheckoutViewModel::class.java)
    }
}