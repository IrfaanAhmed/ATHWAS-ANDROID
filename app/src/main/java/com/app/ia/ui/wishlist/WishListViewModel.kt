package com.app.ia.ui.wishlist

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityWishListBinding
import com.app.ia.dialog.IADialog
import com.app.ia.enums.Status
import com.app.ia.model.FavoriteListResponse
import com.app.ia.utils.Resource
import kotlinx.coroutines.Dispatchers

class WishListViewModel(private val baseRepository: BaseRepository) : BaseViewModel(), LifecycleObserver {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityWishListBinding

    var favoriteList = MutableLiveData<MutableList<FavoriteListResponse.Docs>>()
    private val favoriteListAll = ArrayList<FavoriteListResponse.Docs>()

    val isItemAvailable = MutableLiveData(true)

    val currentPage = MutableLiveData(1)
    val isLastPage = MutableLiveData(false)
    val favPosition = MutableLiveData<Int>()

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
                            if (users.status == "success") {
                                isLastPage.value = (currentPage.value == users.data?.totalpages)
                                favoriteListAll.addAll(users.data?.docs!!)
                                favoriteList.value = favoriteListAll
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
                            if (users.status == "success") {
                                /*val favItem = favoriteList.value!![favPosition.value!!]
                                favItem.isFavourite = if (favItem.isFavourite == 0) 1 else 0
                                favoriteList.value = favoriteList.value*/
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