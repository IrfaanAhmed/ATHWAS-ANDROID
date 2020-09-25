package com.app.ia.ui.favourite_product

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
import com.app.ia.databinding.ActivityFavouriteProductBinding
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.ui.favourite_product.adapter.FavouriteProductAdapter
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.utils.*
import kotlinx.android.synthetic.main.activity_favourite_product.*
import kotlinx.android.synthetic.main.common_header.view.*


class FavouriteProductActivity : BaseActivity<ActivityFavouriteProductBinding, FavouriteProductViewModel>() {

    private var mBinding: ActivityFavouriteProductBinding? = null
    private var mViewModel: FavouriteProductViewModel? = null

    var productAdapter: FavouriteProductAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_favourite_product
    }

    override fun getViewModel(): FavouriteProductViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)

        makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        toolbar.imageViewIcon.visible()
        toolbar.ivSearchIcon.visible()
        toolbar.ivEditProfileIcon.gone()

        recViewFavouriteProduct.addItemDecoration(
            EqualSpacingItemDecoration(
                20,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        recViewFavouriteProduct.addItemDecoration(
            DividerItemDecoration(
                this@FavouriteProductActivity,
                LinearLayout.VERTICAL
            )
        )
        productAdapter = FavouriteProductAdapter()
        recViewFavouriteProduct.adapter = productAdapter
        var categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        categoryList.add("One Plus")
        categoryList.add("iPhone")
        categoryList.add("Motorola")
        categoryList.add("RealMe")
        categoryList.add("MI")
        productAdapter!!.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(
            FavouriteProductViewModel(
                BaseRepository(
                    RetrofitFactory.getInstance(),
                    this
                )
            )
        )
        mViewModel = ViewModelProvider(this, factory).get(FavouriteProductViewModel::class.java)
    }
}