package com.app.ia.ui.search

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivitySearchBinding
import com.app.ia.utils.invisible
import com.app.ia.utils.setOnApplyWindowInset1
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.common_header.view.*

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {

    private var mBinding: ActivitySearchBinding? = null
    private var mViewModel: SearchViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun getViewModel(): SearchViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        //mViewModel?.setIntent(intent)

        setOnApplyWindowInset1(toolbar, content_container)
        toolbar.imageViewIcon.invisible()

        /*recViewSubCategory.addItemDecoration(EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.GRID))
        subCategoryAdapter = SubCategoryListAdapter()
        recViewSubCategory.adapter = subCategoryAdapter

        mViewModel?.productSubCategory?.observe(this, Observer {
            subCategoryAdapter!!.submitList(it)
        })*/
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(SearchViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }
}