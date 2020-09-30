package com.app.ia.apiclient

import com.app.ia.model.BaseResponse
import com.app.ia.model.LoginResponse
import com.app.ia.model.NoDataResponse
import com.app.ia.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
    suspend fun resendOTP(@FieldMap params: Map<String, String>): Response<BaseResponse<NoDataResponse>>

}