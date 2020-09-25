package com.app.ia.ui.delivery_address

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
import com.app.ia.databinding.ActivityDeliveryAddressBinding
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.ui.delivery_address.adapter.DeliveryAddressAdapter
import com.app.ia.ui.product_list.ProductListViewModel
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_delivery_address.*
import kotlinx.android.synthetic.main.common_header.view.*

class DeliveryAddressActivity : BaseActivity<ActivityDeliveryAddressBinding, DeliveryAddressViewModel>() {

    private var mBinding: ActivityDeliveryAddressBinding? = null
    private var mViewModel: DeliveryAddressViewModel? = null

    var addressAdapter: DeliveryAddressAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_delivery_address
    }

    override fun getViewModel(): DeliveryAddressViewModel {
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
        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.invisible()
        toolbar.ivSearchIcon.invisible()

        recViewAddress.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewAddress.addItemDecoration(DividerItemDecoration(this@DeliveryAddressActivity, LinearLayout.VERTICAL))
        addressAdapter = DeliveryAddressAdapter()
        recViewAddress.adapter = addressAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("Work")
        categoryList.add("Home")
        categoryList.add("Office")
        addressAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(DeliveryAddressViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(DeliveryAddressViewModel::class.java)
    }
}