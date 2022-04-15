package com.app.ia.apiclient

import com.app.ia.model.*
import com.app.wallet.tivo.model.ResendOTPResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(Api.LOGIN_URL)
    @FormUrlEncoded
    suspend fun userLogin(@FieldMap params: Map<String, String>): Response<BaseResponse<LoginResponse>>

    @POST(Api.REGISTER_URL)
    @FormUrlEncoded
    suspend fun userRegister(@FieldMap params: Map<String, String>): Response<BaseResponse<RegisterResponse>>

    @POST(Api.VERIFY_OTP)
    @FormUrlEncoded
    suspend fun verifyOTP(@FieldMap params: Map<String, String>): Response<BaseResponse<LoginResponse>>

    @POST(Api.RESEND_OTP)
    @FormUrlEncoded
    suspend fun resendOTP(@FieldMap params: Map<String, String>): Response<BaseResponse<ResendOTPResponse>>

    @POST(Api.UPDATE_FORGOT_PASSWORD_URL)
    @FormUrlEncoded
    suspend fun updateForgotPassword(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.BUSINESS_CATEGORY)
    suspend fun getBusinessCategory(): Response<BaseResponse<BusinessCategoryResponse>>

    @GET(Api.BRAND)
    suspend fun getBrands(@QueryMap request: Map<String, String>): Response<BaseResponse<BrandResponse>>

    @POST(Api.PRODUCT_CATEGORY)
    @FormUrlEncoded
    suspend fun getProductCategory(@FieldMap params: Map<String, String>): Response<BaseResponse<ProductCategoryResponse>>

    @POST(Api.PRODUCT_SUB_CATEGORY)
    @FormUrlEncoded
    suspend fun getProductSubCategory(@FieldMap params: Map<String, String>): Response<BaseResponse<ProductSubCategoryResponse>>

    @POST(Api.PRODUCT_LISTING)
    @FormUrlEncoded
    suspend fun getProductListing(@FieldMap params: Map<String, String>): Response<BaseResponse<ProductListingResponse>>

    @POST(Api.SIMILAR_PRODUCT_LISTING)
    @FormUrlEncoded
    suspend fun getSimilarProductListing(@FieldMap params: Map<String, String>): Response<BaseResponse<SimilarProductListResponse>>

    @GET(Api.FAVORITE_LISTING)
    suspend fun getFavoriteListing(@QueryMap params: Map<String, String>): Response<BaseResponse<FavoriteListResponse>>

    @GET(Api.BANNER_LISTING)
    suspend fun getBannerListing(): Response<BaseResponse<BannerResponse>>

    @GET(Api.DISCOUNT_PRODUCT_LISTING)
    suspend fun getDiscountedProductListing(@QueryMap params: Map<String, String>): Response<BaseResponse<HomeProductListingResponse>>

    @GET(Api.POPULAR_PRODUCT_LISTING)
    suspend fun getPopularProductListing(@QueryMap params: Map<String, String>): Response<BaseResponse<HomeProductListingResponse>>

    @GET(Api.DISCOUNT_PRODUCT_LISTING)
    suspend fun getDiscountedProductListing1(@QueryMap params: Map<String, String>): Response<BaseResponse<ProductListingResponse>>

    @GET(Api.POPULAR_PRODUCT_LISTING)
    suspend fun getPopularProductListing1(@QueryMap params: Map<String, String>): Response<BaseResponse<ProductListingResponse>>

    @POST(Api.PRODUCT_DETAIL)
    @FormUrlEncoded
    suspend fun getProductDetail(@FieldMap params: Map<String, String>): Response<BaseResponse<ProductDetailResponse>>

    @POST(Api.CUSTOMIZATION_PRODUCT)
    @FormUrlEncoded
    suspend fun getCustomizationProductDetail(@FieldMap params: Map<String, String>): Response<CustomizationProductDetail>

    @POST(Api.FAVOURITE_PRODUCT)
    @FormUrlEncoded
    suspend fun addFavorite(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.GET_TRANSACTION_HISTORY)
    suspend fun getTransactionHistory(@QueryMap request: Map<String, String>): Response<BaseResponse<WalletHistoryResponse>>

    @POST(Api.ADD_TO_WALLET)
    @FormUrlEncoded
    suspend fun addToWallet(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.ADD_ADDRESS)
    @FormUrlEncoded
    suspend fun addAddress(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.ADDRESS_LIST)
    suspend fun getAddresses(): Response<BaseResponse<AddressListResponse>>

    @GET("json")
    suspend fun getAddressFromInput(@QueryMap params: Map<String, String>): Response<GetAddressResponse>

    @POST(Api.DELETE_ADDRESS)
    @FormUrlEncoded
    suspend fun deleteAddresses(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.SET_DEFAULT_ADDRESS)
    @FormUrlEncoded
    suspend fun setDefaultAddress(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    suspend fun changePassword(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.GET_PROFILE_URL)
    suspend fun getProfile(): Response<BaseResponse<ProfileResponse>>

    @POST(Api.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    suspend fun updateProfile(@FieldMap params: Map<String, String>): Response<BaseResponse<UpdateProfileResponse>>

    @POST(Api.UPDATE_PROFILE_URL)
    @Multipart
    suspend fun updateProfile(@PartMap() partData: Map<String, @JvmSuppressWildcards RequestBody>,
                              @Part() file: MultipartBody.Part): Response<BaseResponse<UpdateProfileResponse>>

    @GET(Api.NOTIFICATION_URL)
    suspend fun getNotification(@QueryMap params: Map<String, String>): Response<BaseResponse<NotificationResponse>>

    @DELETE(Api.NOTIFICATION_URL)
    suspend fun deleteAllNotification(): Response<BaseResponse<NoDataResponse>>

    @DELETE(Api.DELETE_NOTIFICATION_URL)
    suspend fun deleteNotification(@Path("notification_id") id: String): Response<BaseResponse<NoDataResponse>>

    @POST(Api.ADD_TO_CART)
    @FormUrlEncoded
    suspend fun addToCart(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.NOTIFY_ME)
    @FormUrlEncoded
    suspend fun notifyMe(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.CONTENT_DATA_URL)
    suspend fun getContentData(@Path("content_key") content_key: String): Response<BaseResponse<ContentDataResponse>>

    @GET(Api.CONTENT_DATA_URL)
    suspend fun getFaqData(@Path("content_key") content_key: String): Response<BaseResponse<FaqResponse>>

    @GET(Api.GET_CUSTOMIZATION_TYPE_URL)
    suspend fun customizationType(@Path("category_id") id: String): Response<BaseResponse<CustomizationTypeResponse>>

    @GET(Api.DEAL_DAY_BANNER_LISTING)
    suspend fun dealOfTheDayBanner(): Response<BaseResponse<DealOfTheDayBannerResponse>>

    @GET(Api.DEAL_DAY_BANNER_DETAIL)
    suspend fun dealOfTheDayBannerDetail(@Path("banner_id") id: String, @QueryMap params: Map<String, String>): Response<BaseResponse<DealProductListResponse>>

    @GET(Api.GET_CUSTOMIZATION_SUB_TYPE_URL)
    suspend fun customizationSubType(@Path("customizationtype_id") id: String): Response<BaseResponse<CustomizationSubTypeResponse>>

    @GET(Api.CART_LIST)
    suspend fun getCartListing(@QueryMap params: Map<String, String>): Response<BaseResponse<CartListResponse>>

    @PUT(Api.UPDATE_CART_ITEM)
    @FormUrlEncoded
    suspend fun updateCartItem(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @DELETE(Api.DELETE_CART_ITEM)
    suspend fun deleteCartItem(@Path("cart_id") id: String): Response<BaseResponse<NoDataResponse>>

    @POST(Api.GET_DELIVERY_FEE)
    @FormUrlEncoded
    suspend fun getDeliveryFee(@FieldMap params: Map<String, String>): Response<BaseResponse<DeliveryFeeResponse>>

    @POST(Api.APPLY_COUPON)
    @FormUrlEncoded
    suspend fun applyCoupon(@FieldMap params: Map<String, String>): Response<BaseResponse<PromoCodeResponse>>

    @POST(Api.PLACE_ORDER)
    @FormUrlEncoded
    suspend fun placeOrder(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @GET(Api.ORDER_HISTORY)
    suspend fun orderHistory(@QueryMap params: Map<String, String>): Response<BaseResponse<OrderHistoryResponse>>

    @GET(Api.GET_CART_COUNT)
    suspend fun getCartCount(): Response<BaseResponse<CartCountResponse>>

    @GET(Api.ORDER_PAST_HISTORY)
    suspend fun orderPastHistory(@QueryMap params: Map<String, String>): Response<BaseResponse<OrderHistoryResponse>>

    @GET(Api.ORDER_DETAIL)
    suspend fun orderDetail(@Path("order_id") id: String): Response<BaseResponse<OrderDetailResponse>>

    @GET(Api.OFFER_LIST)
    suspend fun offerList(@QueryMap params: Map<String, String>): Response<BaseResponse<OffersResponse>>

    @POST(Api.CANCEL_GROCERY_ORDER)
    @FormUrlEncoded
    suspend fun cancelGroceryOrder(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.DOWNLOAD_INVOICE)
    @FormUrlEncoded
    suspend fun downloadInvoice(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.CANCEL_ORDER)
    @FormUrlEncoded
    suspend fun cancelOrder(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.RETURN_GROCERY_ORDER)
    @FormUrlEncoded
    suspend fun returnGroceryOrder(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.RETURN_ORDER)
    @FormUrlEncoded
    suspend fun returnOrder(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.RATING_REVIEW)
    @FormUrlEncoded
    suspend fun ratingReview(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

    @POST(Api.GENERATE_ORDER)
    @FormUrlEncoded
    suspend fun generateOrderID(@FieldMap params: Map<String, String>): Response<BaseResponse<OrderIdResponse>>

    @POST(Api.CHECK_PAYMENT_STATUS)
    @FormUrlEncoded
    suspend fun checkPaymentStatus(@FieldMap params: Map<String, String>): Response<BaseResponse<PaymentStatusResponse>>

    @GET(Api.REDEEM_POINTS)
    suspend fun redeemPoints(@QueryMap params: Map<String, String>): Response<BaseResponse<RedeemPointResponse>>

    @POST(Api.RSA_URL)
    @FormUrlEncoded
    suspend fun getRSAKey(@FieldMap params: Map<String, String>): Response<okhttp3.ResponseBody>

    @POST(Api.LOGOUT)
    @FormUrlEncoded
    suspend fun logout(@FieldMap params: Map<String, String>): Response<BaseResponse<Any>>
}