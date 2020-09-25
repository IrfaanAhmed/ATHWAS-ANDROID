package com.app.ia.apiclient

import com.app.ia.callback.GeneralCallback
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

@Suppress("BlockingMethodInNonBlockingContext")
abstract class ApiRequest(private val generalCallback: GeneralCallback) {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {

        val response = call.invoke()
        generalCallback.hideProgress()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val message = StringBuilder()
            val error = response.errorBody()?.string()
            error?.let {
                try {
                    message.append(JSONObject(it).getString("message"))
                } catch (e: JSONException) {
                }
                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")
            throw ApiException(message.toString())
        }
    }
}