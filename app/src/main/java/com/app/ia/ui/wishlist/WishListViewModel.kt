package com.app.ia.ui.wishlist

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityWishListBinding
import com.app.ia.enums.Status
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.FavoriteListResponse
import com.app.ia.utils.CommonUtils
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.android.synthetic.main.common_header.view.*
import kotlinx.coroutines.Dispatchers

class WishListViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityWishListBinding

    var favoriteList = MutableLiveData<MutableList<FavoriteListResponse.Docs>>()
    private val favoriteListAll = ArrayList<FavoriteListResponse.Docs>()

    val isItemAvailable = MutableLiveData(true)

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)

    fun setVariable(mBinding: ActivityWishListBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.wish_list))
        setUpObserver()
    }

    fun setUpObserver() {
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = currentPage.value!!.toString()
        productListingObserver(requestParams)
    }

    private fun getProductListing(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getFavoriteListing(requestParams)))
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
                            isLastPage.value = (currentPage.value == users.data?.totalpages)
                            favoriteListAll.addAll(users.data?.docs!!)
                            favoriteList.value = favoriteListAll
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

    fun addFavorite(product_id: String, position: Int) {
        val requestParams = HashMap<String, String>()
        requestParams["product_id"] = product_id
        requestParams["status"] = "0"
        addFavoriteObserver(requestParams, position)
    }

    private fun addFavourite(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.addFavorite(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun addFavoriteObserver(requestParams: HashMap<String, String>, deletedPosition: Int) {

        addFavourite(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->
                            Toast.makeText(mActivity, users.message, Toast.LENGTH_LONG).show()
                            favoriteListAll.removeAt(deletedPosition)
                            favoriteList.value = favoriteListAll
                            (mActivity as WishListActivity).wishListAdapter?.notifyItemRemoved(deletedPosition)
                            (mActivity as WishListActivity).wishListAdapter?.notifyItemRangeChanged(deletedPosition, favoriteListAll.size)
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
                    }

                    Status.LOADING -> {
                        baseRepository.callback.showProgress()
                    }
                }
            }
        })
    }
}