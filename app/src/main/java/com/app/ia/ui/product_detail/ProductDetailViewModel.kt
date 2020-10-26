package com.app.ia.ui.product_detail

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityProductDetailBinding
import com.app.ia.dialog.IADialog
import com.app.ia.dialog.bottom_sheet_dialog.CustomisationDialogFragment
import com.app.ia.enums.Status
import com.app.ia.model.CustomisationBean
import com.app.ia.model.ProductDetailResponse
import com.app.ia.model.SimilarProductListResponse
import com.app.ia.ui.product_detail.adapter.CustomizationTypeAdapter
import com.app.ia.ui.product_detail.adapter.SimilarProductListAdapter
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class ProductDetailViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityProductDetailBinding

    val isItemAvailable = MutableLiveData(true)
    val titleValue = MutableLiveData("")

    val productDetail = MutableLiveData<ProductDetailResponse.Product>()
    val similarProductList = MutableLiveData<MutableList<SimilarProductListResponse.Docs>>()
    private val customizationTypeAdapter = CustomizationTypeAdapter()
    var similarProductAdapter: SimilarProductListAdapter = SimilarProductListAdapter()


    fun setVariable(mBinding: ActivityProductDetailBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set("")
        val requestParams = HashMap<String, String>()
        requestParams["product_id"] = intent.getStringExtra("product_id")!!
        productDetailObserver(requestParams)
        setCustomizationAdapter()
    }

    private fun setCustomizationAdapter() {
        customizationTypeAdapter.setOnCustomizationClickListener(object : CustomizationTypeAdapter.OnCustomizationClickListener {
            override fun onCustomizationClick(customization: ProductDetailResponse.Product.Customizations, position: Int) {
                onCustomisationClick(customization)
            }
        })
        mBinding.recyclerViewCustomization.adapter = customizationTypeAdapter
        productDetail.observe(mBinding.lifecycleOwner!!, {
            customizationTypeAdapter.submitList(it?.customizations)
            val requestParams = HashMap<String, String>()
            requestParams["sub_category_id"] = it?.subcategory?.Id!!
            getSimilarProductObserver(requestParams)
        })

        mBinding.recViewSimilarProduct.adapter = similarProductAdapter
        similarProductList.observe(mBinding.lifecycleOwner!!, {
            similarProductAdapter.submitList(it)
        })
    }

    fun onCustomisationClick(customization: ProductDetailResponse.Product.Customizations) {
        val bottomSheetFragment = CustomisationDialogFragment(customization, productDetail.value!!.mainProductId)
        bottomSheetFragment.show((mActivity as ProductDetailActivity).supportFragmentManager, bottomSheetFragment.tag)
        /*bottomSheetFragment.setOnItemClickListener(object: ProductFilterDialogFragment.OnProductFilterClickListener{
            override fun onSubmitClick(filterValue: String) {

                *//*mWalletViewModel?.filterBy?.value = filterValue
                    mWalletViewModel?.walletResponseData?.removeObservers(this@WalletActivity)
                    mWalletViewModel?.walletResponseData = MutableLiveData()
                    walletAdapter = null
                    callObserver()
                    mWalletViewModel?.setupObservers()*//*
                }

            })*/

    }

    private fun getCustomisationList(): ArrayList<CustomisationBean> {
        var list = ArrayList<CustomisationBean>()
        list.add(CustomisationBean("2 kg", false))
        list.add(CustomisationBean("5 kg", true))
        list.add(CustomisationBean("10 kg", false))
        list.add(CustomisationBean("15 kg", false))
        list.add(CustomisationBean("20 kg", false))
        list.add(CustomisationBean("25 kg", false))
        list.add(CustomisationBean("30 kg", false))

        return list
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
                            if (users.status == "success") {
                                productDetail.value = users.data?.product!!
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
                            if (users.status == "success") {
                                similarProductList.value = users.data?.docs!!
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
                        //baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}