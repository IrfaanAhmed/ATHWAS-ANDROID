package com.app.ia.ui.my_cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityMyCartBinding
import com.app.ia.ui.my_cart.adapter.CartListAdapter
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.gone
import com.app.ia.utils.setMarginTop
import com.app.ia.utils.visible
import kotlinx.android.synthetic.main.activity_my_cart.*
import kotlinx.android.synthetic.main.common_header.view.*

class MyCartActivity : BaseActivity<ActivityMyCartBinding, MyCartViewModel>() {

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

        recViewCart.addItemDecoration(
            EqualSpacingItemDecoration(
                20,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        recViewCart.addItemDecoration(
            DividerItemDecoration(
                this@MyCartActivity,
                LinearLayout.VERTICAL
            )
        )
        cartAdapter = CartListAdapter()
        recViewCart.adapter = cartAdapter
        var categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        cartAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(
            MyCartViewModel(
                BaseRepository(
                    RetrofitFactory.getInstance(),
                    this
                )
            )
        )
        mViewModel = ViewModelProvider(this, factory).get(MyCartViewModel::class.java)
    }
}