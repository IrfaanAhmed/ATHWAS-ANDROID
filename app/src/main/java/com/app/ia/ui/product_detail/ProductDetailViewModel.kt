package com.app.ia.ui.product_detail

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.dialog.bottom_sheet_dialog.CustomisationDialogFragment
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.ProductDetailResponse
import com.app.ia.model.SimilarProductListResponse
import com.app.ia.ui.offers.OffersActivity
import com.app.ia.ui.product_detail.adapter.CustomizationTypeAdapter
import com.app.ia.ui.product_detail.adapter.SimilarProductListAdapter
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.Resource
import com.app.ia.utils.startActivity
import com.app.ia.utils.toast
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.Dispatchers

class ProductDetailViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductDetailBinding

    val isItemAvailable = MutableLiveData(true)
    val isItemLoaded = MutableLiveData(false)
    val isBelowButtonVisible = MutableLiveData(false)
    val favouriteChangedList = HashMap<String, Boolean>()

    val productDetail = MutableLiveData<ProductDetailResponse.Product>()
    val similarProductList = MutableLiveData<MutableList<SimilarProductListResponse.Docs>>()
    private val customizationTypeAdapter = CustomizationTypeAdapter()
    private var similarProductAdapter: SimilarProductListAdapter = SimilarProductListAdapter()

    var product_id = MutableLiveData("")

    fun setVariable(mBinding: ActivityProductDetailBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
        product_id.value = intent.getStringExtra("product_id")!!
        val requestParams = HashMap<String, String>()
        requestParams["product_id"] = product_id.value!!
        productDetailObserver(requestParams)
        setCustomizationAdapter()
    }

    fun addToCart() {
        if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            (mActivity as ProductDetailActivity).loginDialog()
        } else {
            val requestParams = HashMap<String, String>()
            if (productDetail.value?.quantity!! < 1) {
                requestParams["product_id"] = productDetail.value!!.Id
                notifyMeObserver(requestParams)
            } else {
                requestParams["inventory_id"] = productDetail.value!!.Id
                requestParams["product_id"] = productDetail.value!!.mainProductId
                requestParams["business_category_id"] = productDetail.value!!.businessCategory.Id
                addItemToCartObserver(requestParams)
            }
        }
    }

    fun seeAllOffers() {
        mActivity.startActivity<OffersActivity> {
            putExtra("product_id", product_id.value)
        }
    }

    private fun setCustomizationAdapter() {
        customizationTypeAdapter.setOnCustomizationClickListener(object : CustomizationTypeAdapter.OnCustomizationClickListener {
            override fun onCustomizationClick(customization: ProductDetailResponse.Product.Customizations, position: Int) {
                onCustomisationClick()
            }
        })
        mBinding.recyclerViewCustomization.adapter = customizationTypeAdapter
        productDetail.observe(mBinding.lifecycleOwner!!, {
            customizationTypeAdapter.submitList(it?.customizations)
            val requestParams = HashMap<String, String>()
            requestParams["sub_category_id"] = it?.subcategory?.Id!!
            requestParams["product_id"] = it.Id
            getSimilarProductObserver(requestParams)
        })

        mBinding.recViewSimilarProduct.adapter = similarProductAdapter
        similarProductList.observe(mBinding.lifecycleOwner!!, {
            similarProductAdapter.submitList(it)
        })

        similarProductAdapter.setOnSimilarProductClickListener(object : SimilarProductListAdapter.OnSimilarProductClickListener {
            override fun onSimilarProductClick(customization: SimilarProductListResponse.Docs, position: Int) {
                val requestParams = HashMap<String, String>()
                requestParams["product_id"] = customization.Id
                productDetailObserver(requestParams)
                setCustomizationAdapter()
            }
        })
    }

    var selectedCustomizationPos = -1

    fun onCustomisationClick() {
        val bottomSheetFragment = CustomisationDialogFragment(selectedCustomizationPos, productDetail.value!!.mainProductId)
        bottomSheetFragment.show((mActivity as ProductDetailActivity).supportFragmentManager, bottomSheetFragment.tag)
        bottomSheetFragment.setOnItemClickListener(object : CustomisationDialogFragment.OnCustomizationSelectListener {

            override fun onCustomizationSelect(id: String, pos: Int) {
                if(!id.isNullOrEmpty()){
                    selectedCustomizationPos = pos
                    val requestParams = HashMap<String, String>()
                    requestParams["product_id"] = id
                    productDetailObserver(requestParams)
                }
            }
        })
    }

    private fun getProductDetail(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getProductDetail(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun productDetailObserver(requestParams: HashMap<String, String>) {

        getProductDetail(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            productDetail.value = users.data?.product!!
                            isItemLoaded.value = true
                            isBelowButtonVisible.value = productDetail.value?.isAvailable == 1

                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        mActivity.toast(it.message!!)
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }

    private fun getSimilarProduct(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getSimilarProductListing(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun getSimilarProductObserver(requestParams: HashMap<String, String>) {

        getSimilarProduct(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            //if (users.status == "success") {
                            similarProductList.value = users.data?.docs!!
                            /*} else {
                                IADialog(mActivity, users.message, true)
                            }*/
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                    }

                    Status.LOADING -> {
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }


    fun onFavoriteClick() {

        if (AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            (mActivity as ProductDetailActivity).loginDialog()
        } else {
            val requestParams = HashMap<String, String>()
            requestParams["product_id"] = productDetail.value?.Id!!
            requestParams["status"] = "" + if (productDetail.value?.isFavourite == 0) 1 else 0
            addFavoriteObserver(requestParams)
        }
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
                            //if (users.status == "success") {
                            mActivity.toast(users.message)
                            productDetail.value?.isFavourite = if (productDetail.value?.isFavourite == 0) 1 else 0
                            if (productDetail.value?.isFavourite == 0) {
                                mBinding.cbFavorite.setImageResource(R.drawable.ic_unlike)
                            } else {
                                mBinding.cbFavorite.setImageResource(R.drawable.ic_like)
                            }
                            favouriteChangedList[productDetail.value!!.Id] = productDetail.value?.isFavourite != 0
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
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

    private fun addItemToCartObserver(requestParams: HashMap<String, String>) {

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

    private fun notifyMeObserver(requestParams: HashMap<String, String>) {

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
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}