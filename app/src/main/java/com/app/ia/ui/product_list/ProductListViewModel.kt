package com.app.ia.ui.product_list

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductListBinding
import com.app.ia.dialog.IADialog
import com.app.ia.dialog.bottom_sheet_dialog.CommonSortDialogFragment
import com.app.ia.dialog.bottom_sheet_dialog.ProductFilterDialogFragment
import com.app.ia.enums.Status
import com.app.ia.model.CommonSortBean
import com.app.ia.model.FilterData
import com.app.ia.model.ProductListingResponse
import com.app.ia.utils.AppConstants
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class ProductListViewModel(private val baseRepository: BaseRepository) : BaseViewModel(),
    LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductListBinding

    var productList = MutableLiveData<MutableList<ProductListingResponse.Docs>>()
    private val productListAll = ArrayList<ProductListingResponse.Docs>()

    private var businessCategoryId = MutableLiveData<String>()
    var categoryId = MutableLiveData<String>()
    private var subCategoryId = MutableLiveData<String>()
    var brandId = MutableLiveData("")
    var minPrice = MutableLiveData("")
    var maxPrice = MutableLiveData("")

    val isItemAvailable = MutableLiveData(true)

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    private val filterData = FilterData()
    val favPosition = MutableLiveData<Int>()

    fun setVariable(mBinding: ActivityProductListBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
        businessCategoryId.value = intent.getStringExtra(AppConstants.EXTRA_BUSINESS_CATEGORY_ID)!!
        categoryId.value = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID)!!
        subCategoryId.value = intent.getStringExtra(AppConstants.EXTRA_PRODUCT_SUB_CATEGORY_ID)!!
        setUpObserver()
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
        productListingObserver(requestParams)
    }

    fun onProductFilterClick() {
        val bottomSheetFragment = ProductFilterDialogFragment(businessCategoryId.value!!, filterData)
        bottomSheetFragment.setOnItemClickListener(object : ProductFilterDialogFragment.OnProductFilterClickListener {
            override fun onSubmitClick(filterValue: FilterData) {
                minPrice.value = filterValue.minPrice
                maxPrice.value = filterValue.maxPrice
                categoryId.value = filterValue.categoryId
                brandId.value = if (filterValue.brandId == "-1") "" else filterValue.brandId
                subCategoryId.value = filterValue.subCategoryId
                productListAll.clear()
                setUpObserver()
            }

        })
        bottomSheetFragment.show(
            (mActivity as ProductListActivity).supportFragmentManager,
            bottomSheetFragment.tag
        )
    }

    private val sortParamValue = MutableLiveData("")
    private val sortFilterPosition = MutableLiveData(-1)
    fun onProductSortByClick() {
        val commonSortDialogFragment = CommonSortDialogFragment(getProductSortList())
        commonSortDialogFragment.show((mActivity as ProductListActivity).supportFragmentManager, commonSortDialogFragment.tag)
        commonSortDialogFragment.setOnItemClickListener(object : CommonSortDialogFragment.OnSortOptionClickListener {
            override fun onSortOptionClick(sortValue: String, sortPosition: Int) {
                sortParamValue.value = sortValue
                sortFilterPosition.value = sortPosition
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

        getProductListing(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            if (users.status == "success") {
                                isLastPage.value = (currentPage.value == users.data?.totalPages)
                                productListAll.addAll(users.data?.docs!!)
                                productList.value = productListAll
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    fun addFavorite(product_id: String) {
        val requestParams = HashMap<String, String>()
        requestParams["product_id"] = product_id
        requestParams["status"] = "1"
        addFavourite(requestParams)
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
                            if (users.status == "success") {
                                val favItem = productList.value!![favPosition.value!!]
                                favItem.isFavourite = !favItem.isFavourite
                                productList.value = productList.value
                            } else {
                                IADialog(mActivity, users.message, true)
                            }
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}