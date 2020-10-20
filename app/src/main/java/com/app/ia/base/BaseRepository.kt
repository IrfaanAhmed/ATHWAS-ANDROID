package com.app.ia.base

import com.app.ia.apiclient.ApiRequest
import com.app.ia.apiclient.ApiService
import com.app.ia.callback.GeneralCallback
import com.app.ia.model.*
import com.app.wallet.tivo.model.ResendOTPResponse
import retrofit2.http.FieldMap

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

    suspend fun getProductListing(request: Map<String, String>): BaseResponse<ProductListingResponse> {
        return apiRequest { myApi.getProductListing(request) }
    }

    suspend fun getFavoriteListing(request: Map<String, String>): BaseResponse<FavoriteListResponse> {
        return apiRequest { myApi.getFavoriteListing(request) }
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
}