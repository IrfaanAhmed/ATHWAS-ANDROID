package com.app.ia.apiclient

import com.app.ia.model.*
import com.app.wallet.tivo.model.ResendOTPResponse
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
}