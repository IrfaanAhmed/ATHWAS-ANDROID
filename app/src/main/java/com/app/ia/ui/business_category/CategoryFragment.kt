package com.app.ia.ui.business_category

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentCategoryBinding
import com.app.ia.model.BusinessCategoryBean
import com.app.ia.ui.business_category.adapter.CategoryAdapter
import com.app.ia.ui.home.HomeActivity
import com.app.ia.utils.EqualSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : BaseFragment<FragmentCategoryBinding, CategoryViewModel>() {

    private var mFragmentCategoryBinding: FragmentCategoryBinding? = null
    private var mCategoryViewModel: CategoryViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_category

    override val viewModel: CategoryViewModel
        get() = mCategoryViewModel!!

    companion object {
        fun newInstance(): CategoryFragment {
            val args = Bundle()
            val fragment = CategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mCategoryViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentCategoryBinding = viewDataBinding!!
        mFragmentCategoryBinding?.lifecycleOwner = this
        mCategoryViewModel?.setVariable(mFragmentCategoryBinding!!)

        val categoryList = ArrayList<BusinessCategoryBean>()
        categoryList.add(BusinessCategoryBean("Mobile", R.drawable.mobile_cat                                                                 ))
        categoryList.add(BusinessCategoryBean("Electronics", R.drawable.electronics))
        categoryList.add(BusinessCategoryBean("Pantry", R.drawable.pantry))
        categoryList.add(BusinessCategoryBean("Appliances", R.drawable.applience))

        val categoryAdapter = CategoryAdapter()
        val itemDecoration1 = EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID)
        recViewCategory.adapter = categoryAdapter
        recViewCategory.addItemDecoration(itemDecoration1)
        categoryAdapter.submitList(categoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(CategoryViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mCategoryViewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).setupHeader(getString(R.string.shop_by_category), false)
        }
    }
}