package com.app.ia.ui.write_review

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.R
import com.app.ia.base.BaseRepository
import com.app.ia.base.BaseViewModel
import com.app.ia.databinding.ActivityWriteReviewBinding
import com.app.ia.enums.Status
import com.app.ia.model.OrderDetailResponse
import com.app.ia.utils.Resource
import com.app.ia.utils.toast
import kotlinx.coroutines.Dispatchers

class WriteReviewViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityWriteReviewBinding
    var productDetail = MutableLiveData<OrderDetailResponse.Category.Products>()
    val order_id = MutableLiveData("")

    fun setVariable(mBinding: ActivityWriteReviewBinding, intent: Intent) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
        title.set(mActivity.getString(R.string.write_a_review))
        productDetail.value = intent.getSerializableExtra("productDetail") as OrderDetailResponse.Category.Products
        order_id.value = intent.getStringExtra("order_id")
    }

    fun onSubmitRating() {

        val rating = mBinding.ratingBarReview.rating
        val review = mBinding.editUserReviews.text.toString()
        when {
            rating <= 0 -> {
                mActivity.toast("Please enter rating")
            }
            review.isEmpty() -> {
                mActivity.toast("Please enter review")
            }
            else -> {
                val params = HashMap<String, String>()
                params["product_id"] = productDetail.value?.Id!!
                params["inventory_id"] = productDetail.value?.inventoryId!!
                params["order_id"] = order_id.value!!
                params["rating"] = rating.toString()
                params["review"] = review
                ratingReviewObserver(params)
            }
        }
    }

    private fun ratingReview(requestParams: HashMap<String, String>) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.ratingReview(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    private fun ratingReviewObserver(requestParams: HashMap<String, String>) {

        ratingReview(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { _ ->
                            Toast.makeText(mActivity, "Rating submitted successfully", Toast.LENGTH_LONG).show()
                            mActivity.finish()
                        }
                    }

                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        if (!it.message.isNullOrEmpty()) {
                            Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
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