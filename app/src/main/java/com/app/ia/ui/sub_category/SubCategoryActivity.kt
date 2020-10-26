package com.app.ia.ui.sub_category

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivitySubCategoryBinding
import com.app.ia.ui.sub_category.adapter.SubCategoryListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_sub_category.*
import kotlinx.android.synthetic.main.common_header.view.*

class SubCategoryActivity : BaseActivity<ActivitySubCategoryBinding, SubCategoryViewModel>() {

    private var mBinding: ActivitySubCategoryBinding? = null
    private var mViewModel: SubCategoryViewModel? = null
    private var subCategoryAdapter: SubCategoryListAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sub_category
    }

    override fun getViewModel(): SubCategoryViewModel {
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

        recViewSubCategory.addItemDecoration(EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.GRID))
        subCategoryAdapter = SubCategoryListAdapter()
        recViewSubCategory.adapter = subCategoryAdapter

        mViewModel?.productSubCategory?.observe(this, {
            subCategoryAdapter!!.submitList(it)
        })
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SubCategoryViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SubCategoryViewModel::class.java)
    }
}