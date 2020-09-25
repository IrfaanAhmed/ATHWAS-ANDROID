package com.app.ia.ui.product_list

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
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.common_header.view.*


class ProductListActivity : BaseActivity<ActivityProductListBinding, ProductListViewModel>() {

    private var mBinding: ActivityProductListBinding? = null
    private var mViewModel: ProductListViewModel? = null

    var productAdapter: ProductListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_list
    }

    override fun getViewModel(): ProductListViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        setOnApplyWindowInset1(toolbar, content_container)

        toolbar.imageViewIcon.visible()
        toolbar.ivSearchIcon.visible()
        toolbar.ivEditProfileIcon.gone()

        recViewProduct.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        recViewProduct.addItemDecoration(DividerItemDecoration(this@ProductListActivity, LinearLayout.VERTICAL))
        productAdapter = ProductListAdapter()
        recViewProduct.adapter = productAdapter
        val categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        productAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductListViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductListViewModel::class.java)
    }
}