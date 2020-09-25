package com.app.ia.ui.product_category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityProductCategoryBinding
import com.app.ia.model.BusinessCategoryBean
import com.app.ia.ui.product_category.adapter.ProductCategoryListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_product_category.*
import kotlinx.android.synthetic.main.common_header.view.*

class ProductCategoryActivity : BaseActivity<ActivityProductCategoryBinding, ProductCategoryViewModel>() {

    private var mBinding: ActivityProductCategoryBinding? = null
    private var mViewModel: ProductCategoryViewModel? = null

    var productCategoryAdapter: ProductCategoryListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_category
    }

    override fun getViewModel(): ProductCategoryViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        recViewProductCategory.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID))
        productCategoryAdapter = ProductCategoryListAdapter()
        recViewProductCategory.adapter = productCategoryAdapter
        val categoryList = ArrayList<BusinessCategoryBean>()
        categoryList.add(BusinessCategoryBean("Oppo", R.drawable.oppo))
        categoryList.add(BusinessCategoryBean("Samsung", R.drawable.samsung))
        categoryList.add(BusinessCategoryBean("One Plus", R.drawable.oneplus))
        categoryList.add(BusinessCategoryBean("iPhone", R.drawable.iphone))
        productCategoryAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductCategoryViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductCategoryViewModel::class.java)
    }
}