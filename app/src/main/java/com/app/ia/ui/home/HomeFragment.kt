package com.app.ia.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentHomeBinding
import com.app.ia.ui.home.adapter.HomeCategoryAdapter
import com.app.ia.ui.home.adapter.HomeProductAdapter
import com.app.ia.ui.home.adapter.SlidingImageAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeFragViewModel>() {

    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mHomeViewModel: HomeFragViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_home

    override val viewModel: HomeFragViewModel
        get() = mHomeViewModel!!

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mHomeViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentHomeBinding = viewDataBinding!!
        mFragmentHomeBinding?.lifecycleOwner = this
        mHomeViewModel?.setVariable(mFragmentHomeBinding!!)

        val homeCategoryList = ArrayList<String>()

        homeCategoryList.add("Mobile")
        homeCategoryList.add("Mobile")
        homeCategoryList.add("Mobile")
        homeCategoryList.add("Mobile")
        homeCategoryList.add("Mobile")

        val homeCategoryAdapter = HomeCategoryAdapter()
        val itemDecoration = EqualSpacingItemDecoration(50, EqualSpacingItemDecoration.HORIZONTAL)
        recyclerViewCategory.adapter = homeCategoryAdapter
        recyclerViewCategory.addItemDecoration(itemDecoration)
        recyclerViewCategory.isNestedScrollingEnabled = false

        mHomeViewModel?.businessCategory?.observe(viewLifecycleOwner, {
            homeCategoryAdapter.submitList(it)
        })

        //ViewPager
        val bannerPagerAdapter = SlidingImageAdapter(requireContext(), homeCategoryList)
        viewPagerBanner.adapter = bannerPagerAdapter
        view_pager_indicator.setViewPager(viewPagerBanner)

        //products
        val homeProductAdapter = HomeProductAdapter(false)
        val itemDecoration1 = EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID)
        recyclerViewProduct.adapter = homeProductAdapter
        recyclerViewProduct.addItemDecoration(itemDecoration1)
        recyclerViewProduct.isNestedScrollingEnabled = false
        homeProductAdapter.submitList(homeCategoryList)

        //Discount products
        val homeDiscountProductAdapter = HomeProductAdapter(true)
        val itemDecoration2 = EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID)
        recyclerViewDiscountProduct.adapter = homeDiscountProductAdapter
        recyclerViewDiscountProduct.addItemDecoration(itemDecoration2)
        recyclerViewDiscountProduct.isNestedScrollingEnabled = false
        homeDiscountProductAdapter.submitList(homeCategoryList)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(HomeFragViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mHomeViewModel = ViewModelProvider(this, factory).get(HomeFragViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).setupHeader("", true)
        }
    }
}