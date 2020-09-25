package com.app.ia.apiclient

import com.app.ia.apiclient.Api
import com.app.ia.model.BaseResponse
import com.app.ia.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @POST(Api.LOGIN_URL)
    @FormUrlEncoded
    suspend fun userLogin(@FieldMap params: Map<String, String>): Response<BaseResponse<LoginResponse>>

}