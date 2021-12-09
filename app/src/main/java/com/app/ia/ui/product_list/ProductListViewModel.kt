package com.app.ia.ui.product_list

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.dialog.bottom_sheet_dialog.CommonSortDialogFragment
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.CommonSortBean
import com.app.ia.model.CustomizationSubTypeResponse
import com.app.ia.model.FilterData
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject

class ProductListViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductListBinding

    //Product listing variables
    var productList = MutableLiveData<MutableList<ProductListingResponse.Docs>>()
    val productListAll = ArrayList<ProductListingResponse.Docs>()

    //Variable to check product available or not
    val isItemAvailable = MutableLiveData(true)

    var businessCategoryId = MutableLiveData<String>()
    var categoryId = MutableLiveData<String>()
    var subCategoryId = MutableLiveData<String>()
    var brandId = MutableLiveData("")
    var minPrice = MutableLiveData("")
    var maxPrice = MutableLiveData("")
    var rating = MutableLiveData("")

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    //Filters selected fields
    private var filterData = FilterData()
    val favPosition = MutableLiveData<Int>()

    //Default category and sub category IDs if reset the filter
    private var constCategoryId = ""
    private var constSubCategoryId = ""

    var bannerId = MutableLiveData("")

    var type = 0

    var isLoading = true

    fun setVariable(mBinding: ActivityProductListBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")

        type = intent.getIntExtra("isPopularOrDiscounted", 0)

        when (type) {
            0 -> {
                mBinding.llFilterView.visibility = View.VISIBLE
                constCategoryId = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID)!!
                constSubCategoryId = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_SUB_CATEGORY_ID)!!
                businessCategoryId.value = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID)!!

                categoryId.value = constCategoryId
                subCategoryId.value = constSubCategoryId
                filterData.categoryId = constCategoryId
                filterData.subCategoryId = constSubCategoryId
            }
            3 -> {
                bannerId.value = intent.getStringExtra("banner_id")!!
                mBinding.llFilterView.visibility = View.GONE
            }
            else -> {
                mBinding.llFilterView.visibility = View.GONE
            }
        }
        onStart()
    }

    fun onStart(){
        currentPage.value = 1
        isLastPage.value = false
        productListAll.clear()
        when (type) {
            0 -> {
                mBinding.llFilterView.visibility = View.VISIBLE

                categoryId.value = constCategoryId
                subCategoryId.value = constSubCategoryId
                filterData.categoryId = constCategoryId
                filterData.subCategoryId = constSubCategoryId
                setUpObserver()
            }
            3 -> {
                mBinding.llFilterView.visibility = View.GONE
                dealOfTheDayBannerProductObserver(bannerId.value!!)
            }
            else -> {
                mBinding.llFilterView.visibility = View.GONE
                popularDiscountedProductObserver()
            }
        }
    }

    fun setUpObserver() {
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = businessCategoryId.value!!
        requestParams["category_id"] = categoryId.value!!
        requestParams["sub_category_id"] = subCategoryId.value!!
        requestParams["page_no"] = currentPage.value!!.toString()
        requestParams["keyword"] = ""
        requestParams["brand_id"] = brandId.value!!
        requestParams["price_start"] = minPrice.value!!
        requestParams["price_end"] = maxPrice.value!!
        requestParams["sortby"] = sortParamValue.value!!
        if (rating.value!!.isEmpty()) {
            requestParams["rating"] = rating.value!!
        } else {
            requestParams["rating"] = (rating.value!!.toDouble()).toInt().toString()
        }

        if (filterData.customizationSelectedFilters.size > 0) {

            val mainArray = JSONArray()
            val mainJsonObject = JSONObject()
            val filtersData = filterData.customizationSelectedFilters
            for (key in filtersData.keys) {
                val filterArray = JSONArray()
                val filters: MutableList<CustomizationSubTypeResponse.CustomizationSubType> = filtersData[key]!!
                for (filter in filters) {
                    if (filter.isSelected) {
                        filterArray.put(filter.Id)
                    }
                }
                mainJsonObject.put(key, filterArray)
            }
            mainArray.put(mainJsonObject)
            requestParams["filter"] = mainArray.toString()
        } else {
            requestParams["filter"] = JsonArray().toString()
        }
        productListingObserver(requestParams)
    }

    fun onProductFilterClick() {

        if (filterData.categoryId.isEmpty() || filterData.categoryId == "-1") {
            filterData.categoryId = constCategoryId
        }

        if (filterData.subCategoryId.isEmpty() || filterData.subCategoryId == "-1") {
            filterData.subCategoryId = constSubCategoryId
        }

        val bottomSheetFragment = ProductFilterDialogFragment(businessCategoryId.value!!, filterData)
        bottomSheetFragment.setOnItemClickListener(object : ProductFilterDialogFragment.OnProductFilterClickListener {
            override fun onSubmitClick(filterValue: FilterData) {
                filterData = filterValue
                minPrice.value = filterValue.minPrice
                maxPrice.value = filterValue.maxPrice
                rating.value = filterValue.ratingPosition.toString()
                if (filterValue.categoryId.isEmpty() || filterValue.categoryId == "-1") {
                    categoryId.value = constCategoryId
                } else {
                    categoryId.value = filterValue.categoryId
                }

                brandId.value = if (filterValue.brandId == "-1") "" else filterValue.brandId

                if (filterValue.subCategoryId.isEmpty() || filterValue.subCategoryId == "-1") {
                    subCategoryId.value = constSubCategoryId
                } else {
                    subCategoryId.value = filterValue.subCategoryId
                }

                //(mActivity as ProductListActivity).resetPaginationOnFilterSet()
                currentPage.value = 1
                productListAll.clear()
                //(mActivity as ProductListActivity).recyclerViewPaging.reset()
                setUpObserver()
            }
        })
        bottomSheetFragment.show((mActivity as ProductListActivity).supportFragmentManager, "filter_dialog")
    }

    private val sortParamValue = MutableLiveData("")
    private val sortFilterPosition = MutableLiveData(0)

    fun onProductSortByClick() {
        val commonSortDialogFragment = CommonSortDialogFragment(getProductSortList())
        commonSortDialogFragment.show((mActivity as ProductListActivity).supportFragmentManager, commonSortDialogFragment.tag)
        commonSortDialogFragment.setOnItemClickListener(object : CommonSortDialogFragment.OnSortOptionClickListener {
            override fun onSortOptionClick(sortValue: String, sortPosition: Int) {
                sortParamValue.value = sortValue
                sortFilterPosition.value = sortPosition
                //(mActivity as ProductListActivity).resetPaginationOnFilterSet()
                currentPage.value = 1
                productListAll.clear()
                setUpObserver()
            }
        })
    }

    private fun getProductSortList(): ArrayList<CommonSortBean> {
        val list = ArrayList<CommonSortBean>()
        list.add(CommonSortBean(mActivity.getString(R.string.popularity), sortFilterPosition.value == 0))
        list.add(CommonSortBean(mActivity.getString(R.string.price_low_to_high), sortFilterPosition.value == 1))
        list.add(CommonSortBean(mActivity.getString(R.string.price_high_to_low), sortFilterPosition.value == 2))
        list.add(CommonSortBean(mActivity.getString(R.string.newest_first), sortFilterPosition.value == 3))
        return list
    }

    private fun getProductListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductListing(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun productListingObserver(requestParams: HashMap<String, String>) {


        isLoading = true

        getProductListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            productListAll.addAll(users.data?.docs!!)
                            productList.value = productListAll
                            isItemAvailable.value = productList.value!!.size > 0
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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

    fun addFavorite(product_id: String, status: Int) {
        val requestParams = HashMap<String, String>()
        requestParams["product_id"] = product_id
        requestParams["status"] = "" + status
        addFavoriteObserver(requestParams)
    }

    private fun addFavourite(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.addFavorite(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun addFavoriteObserver(requestParams: HashMap<String, String>) {

        addFavourite(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            val favItem = productList.value!![favPosition.value!!]
                            favItem.isFavourite = if (favItem.isFavourite == 0) 1 else 0
                            productList.value = productList.value
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

    private fun addItemToCart(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.addToCart(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addItemToCartObserver(requestParams: HashMap<String, String>) {

        addItemToCart(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
                            AppPreferencesHelper.getInstance().cartItemCount = users.data?.cartCount!!
                            CommonUtils.showCartItemCount(mBinding.toolbar.bottom_navigation_notification)
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

    /**
     *  Notify Me Observer
     */
    private fun notifyMe(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.notifyMe(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun notifyMeObserver(requestParams: HashMap<String, String>) {
        notifyMe(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            mActivity.toast(users.message)
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

    /*
    *  Check for popular or discounted product
    * */
    private fun getPopularOrDiscountedProductListing(params: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            if (type == 1) {
                emit(Resource.success(data = baseRepository.getPopularProductListing1(params)))
            } else if (type == 2) {
                emit(Resource.success(data = baseRepository.getDiscountedProductListing1(params)))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun popularDiscountedProductObserver() {

        val params = HashMap<String, String>()
        params["page_no"] = currentPage.value!!.toString()
        params["limit"] = "10"

        isLoading = true

        getPopularOrDiscountedProductListing(params).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            isLastPage.value = (currentPage.value == users.data?.totalPages)
                            productListAll.addAll(users.data?.docs!!)
                            productList.value = productListAll
                            isItemAvailable.value = productList.value!!.size > 0
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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


    /*
    *  Check for Deal of the day banner
    * */
    private fun getDealOfTheDayBannerProductListing(params: HashMap<String, String>, banner_id: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.dealOfTheDayBannerDetail(params, banner_id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun dealOfTheDayBannerProductObserver(banner_id: String) {

        val params = HashMap<String, String>()
        params["page_no"] = currentPage.value!!.toString()
        params["limit"] = "10"

        isLoading = true
        getDealOfTheDayBannerProductListing(params, banner_id).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading = false
                        resource.data?.let { users ->
                            isLastPage.value = true
                            productListAll.addAll(users.data?.productInventoriesData!!)
                            productList.value = productListAll
                            isItemAvailable.value = productList.value!!.size > 0
                        }
                    }

                    Status.ERROR -> {
                        isLoading = false
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