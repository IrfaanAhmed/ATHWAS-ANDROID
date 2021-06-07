package com.app.ia.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.FragmentHomeBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.BannerResponse
import com.app.ia.model.DealOfTheDayBannerResponse
import com.app.ia.ui.home.adapter.DealOfTheDayBannerAdapter
import com.app.ia.ui.home.adapter.HomeCategoryAdapter
import com.app.ia.ui.home.adapter.HomeProductAdapter
import com.app.ia.ui.home.adapter.SlidingImageAdapter
import com.app.ia.ui.product_list.ProductListActivity
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.AppConstants.EXTRA_VOICE_TEXT
import com.app.ia.utils.AppRequestCode
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.startActivity
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

        val homeCategoryAdapter = HomeCategoryAdapter()
        val itemDecoration = EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.HORIZONTAL)
        recyclerViewCategory.adapter = homeCategoryAdapter
        recyclerViewCategory.addItemDecoration(itemDecoration)
        recyclerViewCategory.isNestedScrollingEnabled = false



        mHomeViewModel?.businessCategory?.observe(viewLifecycleOwner, {
            homeCategoryAdapter.submitList(it)
        })

        //ViewPager
        mHomeViewModel?.bannerList?.observe(viewLifecycleOwner, {
            val bannerPagerAdapter = SlidingImageAdapter(requireContext(), it!!)
            viewPagerBanner.adapter = bannerPagerAdapter
            view_pager_indicator.setViewPager(viewPagerBanner)
            bannerPagerAdapter.setOnImageClickListener(object : SlidingImageAdapter.OnImageClickListener {
                override fun onImageClick(view: View, image: BannerResponse.Docs) {
                    AppPreferencesHelper.getInstance().setString(AppPreferencesHelper.CATEGORY, "")
                    requireActivity().startActivity<ProductListActivity> {
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID, image.businessCategory.Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID, image.category.Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_SUB_CATEGORY_ID, image.subcategory.Id)
                    }
                }
            })
        })

        //Deal Of the Day Banner
        mHomeViewModel?.dealOfTheDayBannerList?.observe(viewLifecycleOwner, {

            if (it.size <= 1) {
                leftArrow.visibility = View.GONE
                rightArrow.visibility = View.GONE
            } else {
                leftArrow.visibility = View.GONE
                rightArrow.visibility = View.VISIBLE
            }
            val bannerPagerAdapter = DealOfTheDayBannerAdapter(requireContext(), it!!)
            vpDealOfTheDayBanner.adapter = bannerPagerAdapter
            vpDealOfTheDayBannerIndicator.setViewPager(vpDealOfTheDayBanner)
            bannerPagerAdapter.setOnImageClickListener(object : DealOfTheDayBannerAdapter.OnImageClickListener {
                override fun onImageClick(view: View, image: DealOfTheDayBannerResponse.Docs) {
                    /*AppPreferencesHelper.getInstance().setString(AppPreferencesHelper.CATEGORY, "")
                    requireActivity().startActivity<ProductListActivity> {
                        putExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID, image.businessCategory.Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID, image.category.Id)
                        putExtra(AppConstants.EXTRA_PRODUCT_SUB_CATEGORY_ID, image.subcategory.Id)
                    }*/
                    requireActivity().startActivity<ProductListActivity> {
                        putExtra("isPopularOrDiscounted", 3)
                        putExtra("banner_id", image.Id)
                        //1 for popular
                        //2 for discounted
                    }
                }
            })
        })

        vpDealOfTheDayBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (mHomeViewModel?.dealOfTheDayBannerList?.value!!.size > 1) {
                    when (position) {
                        0 -> {
                            leftArrow.visibility = View.GONE
                            rightArrow.visibility = View.VISIBLE
                        }
                        (mHomeViewModel?.dealOfTheDayBannerList?.value!!.size - 1) -> {
                            leftArrow.visibility = View.VISIBLE
                            rightArrow.visibility = View.GONE
                        }
                        else -> {
                            leftArrow.visibility = View.VISIBLE
                            rightArrow.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        leftArrow.setOnClickListener {
            vpDealOfTheDayBanner.currentItem = vpDealOfTheDayBanner.currentItem - 1
        }

        rightArrow.setOnClickListener {
            vpDealOfTheDayBanner.currentItem = vpDealOfTheDayBanner.currentItem + 1
        }

        //products
        val homeProductAdapter = HomeProductAdapter()
        val itemDecoration1 = EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID)
        recyclerViewProduct.adapter = homeProductAdapter
        recyclerViewProduct.addItemDecoration(itemDecoration1)
        recyclerViewProduct.isNestedScrollingEnabled = false

        mHomeViewModel?.popularProductListing?.observe(viewLifecycleOwner, {
            homeProductAdapter.submitList(it)
        })

        //Discount products
        val homeDiscountProductAdapter = HomeProductAdapter()
        val itemDecoration2 = EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.GRID)
        recyclerViewDiscountProduct.adapter = homeDiscountProductAdapter
        recyclerViewDiscountProduct.addItemDecoration(itemDecoration2)
        recyclerViewDiscountProduct.isNestedScrollingEnabled = false

        mHomeViewModel?.discountedProductListing?.observe(viewLifecycleOwner, {
            homeDiscountProductAdapter.submitList(it)
        })

        ivMicIcon.setOnClickListener { startSpeechRecognizer() }

        mSwipeRefresh.setOnRefreshListener {
            if (mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = false
            }
            mHomeViewModel?.businessCategoryObserver()
            mHomeViewModel?.bannerObserver()
            mHomeViewModel?.dealOfTheDayObserver()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppRequestCode.REQUEST_INSTRUCTION_SPEECH_RECOGNIZER) {
            if (resultCode == Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                requireActivity().startActivity<SearchActivity> {
                    putExtra(EXTRA_VOICE_TEXT, results!![0])
                }
            }
        }
    }

    private fun startSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.what_are_you_looking_for))
        startActivityForResult(intent, AppRequestCode.REQUEST_INSTRUCTION_SPEECH_RECOGNIZER)
    }
}