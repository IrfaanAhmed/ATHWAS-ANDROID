package com.app.ia.apiclient

import android.app.Activity
import android.content.Intent
import com.app.ia.IAApplication
import com.app.ia.callback.GeneralCallback
import com.app.ia.dialog.IADialog
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.login.LoginActivity
import com.app.ia.utils.AppRequestCode
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
                    val errorMsg = JSONObject(it).getString("message")
                    message.append(errorMsg)

                    if(response.code() == 401) {
                        val iaDialog = IADialog(IAApplication.getInstance().getCurrentActivity()!!, errorMsg, true)
                        iaDialog.setOnItemClickListener(object : IADialog.OnClickListener{
                            override fun onPositiveClick() {
                                AppPreferencesHelper.getInstance().clearAllPreferences()
                                val intent = Intent(IAApplication.getInstance().applicationContext, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                IAApplication.getInstance().applicationContext.startActivity(intent)
                            }

                            override fun onNegativeClick() {
                            }
                        })
                    }
                    //{"status":"error","api_name":"/user_service/customer/product/get_product_detail/5f902c69bf0a9a4f8704424b","message":"Login session expired. Please login again to continue.","data":{}}
                } catch (e: JSONException) {
                }
                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")
            throw ApiException(message.toString())
        }
    }
}