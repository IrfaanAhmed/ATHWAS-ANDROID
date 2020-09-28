package com.app.ia.base

import com.app.ia.apiclient.ApiRequest
import com.app.ia.apiclient.ApiService
import com.app.ia.callback.GeneralCallback
import com.app.ia.model.BaseResponse
import com.app.ia.model.LoginResponse
import com.app.ia.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

open class BaseRepository(private val myApi: ApiService, generalCallback: GeneralCallback) : ApiRequest(generalCallback) {

    val callback = generalCallback

    suspend fun userLogin(request: Map<String, String>): BaseResponse<LoginResponse> {
        return apiRequest { myApi.userLogin(request) }
    }

    suspend fun userRegister(request: Map<String, String>): BaseResponse<RegisterResponse> {
        return apiRequest { myApi.userRegister(request) }
    }
}