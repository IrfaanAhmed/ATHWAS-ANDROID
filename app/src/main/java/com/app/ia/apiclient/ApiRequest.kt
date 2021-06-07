package com.app.ia.apiclient

import android.content.Intent
import com.app.ia.IAApplication
import com.app.ia.callback.GeneralCallback
import com.app.ia.dialog.IADialog
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.ui.login.LoginActivity
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
                    val errorMsg : String? = JSONObject(it).optString("message")
                    if(!errorMsg.isNullOrEmpty()) {
                        message.append(errorMsg)
                    } else {
                        message.append("Session Expired.")
                    }

                    if (response.code() == 401 || response.code() == 403) {
                        val iaDialog = IADialog(IAApplication.getInstance().getCurrentActivity()!!, if(errorMsg.isNullOrEmpty()) "Session expired" else errorMsg, true)
                        iaDialog.setOnItemClickListener(object : IADialog.OnClickListener {
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
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            throw ApiException(message.toString())
        }
    }
}