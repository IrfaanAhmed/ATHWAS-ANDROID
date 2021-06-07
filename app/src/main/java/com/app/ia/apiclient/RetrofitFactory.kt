package com.app.ia.apiclient

import com.app.ia.IAApplication
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.utils.CryptLib
import com.app.ia.utils.NetworkConnectionInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


object RetrofitFactory {

    private var apiService: ApiService? = null
    private var googleApiService: ApiService? = null

    fun getInstance(): ApiService {
        return apiService ?: makeRetrofitService("")
    }

    fun getGoogleInstance(): ApiService {
        return googleApiService ?: makeRetrofitService(Api.GET_ADDRESS_FROM_INPUT)
    }

    fun getPaymentInstance(): ApiService {
        return apiService ?: makePaymentRetrofitService()
    }

    private fun makeRetrofitService(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(if (baseUrl.isEmpty()) Api.BASE_URL else baseUrl)
            .client(if (baseUrl.isEmpty()) makeOkHttpClient() else makeGoogleOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

    private fun makePaymentRetrofitService(): ApiService {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .client(makeGoogleOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

    private fun makeOkHttpClient(): OkHttpClient {
        val tlsSocketFactory = TLSSocketFactory()
        val networkConnectionInterceptor = NetworkConnectionInterceptor(IAApplication.getInstance())
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(makeLoggingInterceptor())
            .addInterceptor(networkConnectionInterceptor)
            .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private fun makeGoogleOkHttpClient(): OkHttpClient {
        val tlsSocketFactory = TLSSocketFactory()
        val networkConnectionInterceptor = NetworkConnectionInterceptor(IAApplication.getInstance())
        return OkHttpClient.Builder()
            .addInterceptor(makeLoggingInterceptor())
            .addInterceptor(networkConnectionInterceptor)
            .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private val authInterceptor = Interceptor { chain ->

        val cryptLib = CryptLib()
        val cipherText = cryptLib.encryptPlainTextWithRandomIV(Date().time.toString(), "keMStjdies")
        val newRequest : Request = if(AppPreferencesHelper.getInstance().authToken.isEmpty()) {
            chain.request()
                .newBuilder()
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("X-Access-Token", cipherText.trim())
                .build()
        } else {
            chain.request()
                .newBuilder()
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("X-Access-Token", cipherText.trim())
                .addHeader("Authorization", "Bearer ${AppPreferencesHelper.getInstance().authToken}")
                .addHeader("login_user_id", AppPreferencesHelper.getInstance().userID)
                .build()
        }
        chain.proceed(newRequest)
    }
}