package com.app.ia.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.FragmentHomeBinding
import com.app.ia.enums.Status
import com.app.ia.model.BannerResponse
import com.app.ia.model.BusinessCategoryResponse
import com.app.ia.model.DealOfTheDayBannerResponse
import com.app.ia.model.HomeProductListingResponse
import com.app.ia.ui.product_list.ProductListActivity
import com.app.ia.ui.search.SearchActivity
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivity
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class HomeFragViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: FragmentHomeBinding
    var businessCategory = MutableLiveData<MutableList<BusinessCategoryResponse.Docs>>()
    var bannerList = MutableLiveData<MutableList<BannerResponse.Docs>>()
    var dealOfTheDayBannerList = MutableLiveData<MutableList<DealOfTheDayBannerResponse.Docs>>()
    var popularProductListing = MutableLiveData<MutableList<HomeProductListingResponse.Docs>>()
    var discountedProductListing = MutableLiveData<MutableList<HomeProductListingResponse.Docs>>()

    fun setVariable(mBinding: FragmentHomeBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        businessCategoryObserver()
        bannerObserver()
        dealOfTheDayObserver()

        val params = HashMap<String, String>()
        params["page_no"] = "1"
        params["limit"] = "6"
        discountedProductObserver(params)
        popularProductObserver(params)
    }

    fun onViewAllClick(type: Int) {
        mActivity.startActivity<ProductListActivity> {
            putExtra("isPopularOrDiscounted", type)
            //1 for popular
            //2 for discounted
        }
    }

    fun onSearchProductClick() {
        mActivity.startActivity<SearchActivity>()
    }

    private fun getBusinessCategory() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getBusinessCategory()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun businessCategoryObserver() {
        getBusinessCategory().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            businessCategory.value = users.data?.docs!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getBannerListing() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getBannerListing()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun bannerObserver() {
        getBannerListing().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            bannerList.value = users.data?.docs!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getPopularProductListing(params: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getPopularProductListing(params)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun popularProductObserver(params: HashMap<String, String>) {
        getPopularProductListing(params).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            popularProductListing.value = users.data?.docs!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


    private fun getDiscountedProductListing(params: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getDiscountedProductListing(params)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun discountedProductObserver(params: HashMap<String, String>) {
        getDiscountedProductListing(params).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            discountedProductListing.value = users.data?.docs!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    /****
     *
     * */
    private fun getDealOfTheDayBannerListing() = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.dealOfTheDayBanner()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun dealOfTheDayObserver() {
        getDealOfTheDayBannerListing().observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            dealOfTheDayBannerList.value = users.data?.docs!!
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            mActivity.toast(it.message)
                        }
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}