package com.app.ia.base

import com.app.ia.apiclient.ApiRequest
import com.app.ia.apiclient.ApiService
import com.app.ia.callback.GeneralCallback
import com.app.ia.model.*
import com.app.wallet.tivo.model.ResendOTPResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.HashMap

open class BaseRepository(private val myApi: ApiService, generalCallback: GeneralCallback) : ApiRequest(generalCallback) {

    val callback = generalCallback

    suspend fun userLogin(request: Map<String, String>): BaseResponse<LoginResponse> {
        return apiRequest { myApi.userLogin(request) }
    }

    suspend fun userRegister(request: Map<String, String>): BaseResponse<RegisterResponse> {
        return apiRequest { myApi.userRegister(request) }
    }

    suspend fun verifyOTP(request: Map<String, String>): BaseResponse<LoginResponse> {
        return apiRequest { myApi.verifyOTP(request) }
    }

    suspend fun resendOTP(request: Map<String, String>): BaseResponse<ResendOTPResponse> {
        return apiRequest { myApi.resendOTP(request) }
    }

    suspend fun updateForgotPassword(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.updateForgotPassword(request) }
    }

    suspend fun getBusinessCategory(): BaseResponse<BusinessCategoryResponse> {
        return apiRequest { myApi.getBusinessCategory() }
    }

    suspend fun getProductCategory(request: Map<String, String>): BaseResponse<ProductCategoryResponse> {
        return apiRequest { myApi.getProductCategory(request) }
    }

    suspend fun getProductSubCategory(request: Map<String, String>): BaseResponse<ProductSubCategoryResponse> {
        return apiRequest { myApi.getProductSubCategory(request) }
    }

    suspend fun getBrands(request: Map<String, String>): BaseResponse<BrandResponse> {
        return apiRequest { myApi.getBrands(request) }
    }

    suspend fun getProductListing(request: Map<String, String>): BaseResponse<ProductListingResponse> {
        return apiRequest { myApi.getProductListing(request) }
    }

    suspend fun getSimilarProductListing(request: Map<String, String>): BaseResponse<SimilarProductListResponse> {
        return apiRequest { myApi.getSimilarProductListing(request) }
    }

    suspend fun getFavoriteListing(request: Map<String, String>): BaseResponse<FavoriteListResponse> {
        return apiRequest { myApi.getFavoriteListing(request) }
    }

    suspend fun getBannerListing(): BaseResponse<BannerResponse> {
        return apiRequest { myApi.getBannerListing() }
    }

    suspend fun getDiscountedProductListing(params: Map<String, String>): BaseResponse<HomeProductListingResponse> {
        return apiRequest { myApi.getDiscountedProductListing(params) }
    }

    suspend fun getPopularProductListing(params: Map<String, String>): BaseResponse<HomeProductListingResponse> {
        return apiRequest { myApi.getPopularProductListing(params) }
    }

    suspend fun getDiscountedProductListing1(params: Map<String, String>): BaseResponse<ProductListingResponse> {
        return apiRequest { myApi.getDiscountedProductListing1(params) }
    }

    suspend fun getPopularProductListing1(params: Map<String, String>): BaseResponse<ProductListingResponse> {
        return apiRequest { myApi.getPopularProductListing1(params) }
    }

    suspend fun getProductDetail(request: Map<String, String>): BaseResponse<ProductDetailResponse> {
        return apiRequest { myApi.getProductDetail(request) }
    }

    suspend fun addFavorite(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.addFavorite(request) }
    }

    suspend fun getTransactionHistory(request: Map<String, String>): BaseResponse<WalletHistoryResponse> {
        return apiRequest { myApi.getTransactionHistory(request) }
    }

    suspend fun addToWallet(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.addToWallet(request) }
    }

    suspend fun addAddress(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.addAddress(request) }
    }

    suspend fun getAddresses(): BaseResponse<AddressListResponse> {
        return apiRequest { myApi.getAddresses() }
    }

    suspend fun deleteAddress(params: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteAddresses(params) }
    }

    suspend fun setDefaultAddress(params: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.setDefaultAddress(params) }
    }

    suspend fun changePassword(params: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.changePassword(params) }
    }

    suspend fun getProfile(): BaseResponse<ProfileResponse> {
        return apiRequest { myApi.getProfile() }
    }

    suspend fun updateProfile(request: Map<String, String>): BaseResponse<UpdateProfileResponse> {
        return apiRequest { myApi.updateProfile(request) }
    }

    suspend fun updateProfile(partData: Map<String, RequestBody>, file: MultipartBody.Part): BaseResponse<UpdateProfileResponse> {
        return apiRequest { myApi.updateProfile(partData, file) }
    }

    suspend fun getNotification(request: Map<String, String>): BaseResponse<NotificationResponse> {
        return apiRequest { myApi.getNotification(request) }
    }

    suspend fun deleteNotification(notification_id: String): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteNotification(notification_id) }
    }

    suspend fun deleteAllNotification(): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteAllNotification() }
    }

    suspend fun getContentData(request: String): BaseResponse<ContentDataResponse> {
        return apiRequest { myApi.getContentData(request) }
    }

    suspend fun getRSAKey(request: HashMap<String, String>): ResponseBody {
        return apiRequest { myApi.getRSAKey(request) }
    }

    suspend fun getFaqData(request: String): BaseResponse<FaqResponse> {
        return apiRequest { myApi.getFaqData(request) }
    }

    suspend fun getCustomizationType(category_id: String): BaseResponse<CustomizationTypeResponse> {
        return apiRequest { myApi.customizationType(category_id) }
    }

    suspend fun dealOfTheDayBanner(): BaseResponse<DealOfTheDayBannerResponse> {
        return apiRequest { myApi.dealOfTheDayBanner() }
    }

    suspend fun dealOfTheDayBannerDetail(request: Map<String, String>, banner_id: String): BaseResponse<DealProductListResponse> {
        return apiRequest { myApi.dealOfTheDayBannerDetail(banner_id, request) }
    }

    suspend fun getCustomizationSubType(customization_type_id: String): BaseResponse<CustomizationSubTypeResponse> {
        return apiRequest { myApi.customizationSubType(customization_type_id) }
    }

    /*Add to Cart*/
    suspend fun addToCart(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.addToCart(request) }
    }

    suspend fun notifyMe(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.notifyMe(request) }
    }

    suspend fun getCartListing(request: Map<String, String>): BaseResponse<CartListResponse> {
        return apiRequest { myApi.getCartListing(request) }
    }

    suspend fun updateCartItem(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.updateCartItem(request) }
    }

    suspend fun deleteCartItem(cart_id: String): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.deleteCartItem(cart_id) }
    }

    suspend fun placeOrder(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.placeOrder(request) }
    }

    suspend fun cancelGroceryOrder(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.cancelGroceryOrder(request) }
    }

    suspend fun downloadInvoice(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.downloadInvoice(request) }
    }

    suspend fun cancelOrder(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.cancelOrder(request) }
    }

    suspend fun returnGroceryOrder(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.returnGroceryOrder(request) }
    }

    suspend fun returnOrder(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.returnOrder(request) }
    }

    suspend fun getDeliveryFee(request: Map<String, String>): BaseResponse<DeliveryFeeResponse> {
        return apiRequest { myApi.getDeliveryFee(request) }
    }

    suspend fun applyCoupon(request: Map<String, String>): BaseResponse<PromoCodeResponse> {
        return apiRequest { myApi.applyCoupon(request) }
    }

    suspend fun orderHistory(request: Map<String, String>): BaseResponse<OrderHistoryResponse> {
        return apiRequest { myApi.orderHistory(request) }
    }

    suspend fun orderPastHistory(request: Map<String, String>): BaseResponse<OrderHistoryResponse> {
        return apiRequest { myApi.orderPastHistory(request) }
    }

    suspend fun orderDetail(category_id: String): BaseResponse<OrderDetailResponse> {
        return apiRequest { myApi.orderDetail(category_id) }
    }

    suspend fun ratingReview(request: Map<String, String>): BaseResponse<NoDataResponse> {
        return apiRequest { myApi.ratingReview(request) }
    }

    suspend fun generateOrderID(request: Map<String, String>): BaseResponse<OrderIdResponse> {
        return apiRequest { myApi.generateOrderID(request) }
    }

    suspend fun checkPaymentStatus(request: Map<String, String>): BaseResponse<PaymentStatusResponse> {
        return apiRequest { myApi.checkPaymentStatus(request) }
    }

    suspend fun offerList(request: Map<String, String>): BaseResponse<OffersResponse> {
        return apiRequest { myApi.offerList(request) }
    }

    suspend fun redeemPoints(request: Map<String, String>): BaseResponse<RedeemPointResponse> {
        return apiRequest { myApi.redeemPoints(request) }
    }

    suspend fun getCartCount(): BaseResponse<CartCountResponse> {
        return apiRequest { myApi.getCartCount() }
    }
}