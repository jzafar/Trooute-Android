package com.travel.trooute.core.interceptor

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.presentation.ui.auth.SignInActivity
import com.travel.trooute.presentation.utils.showErrorMessage
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor(
    private val context: Context,
    private val sharedPreferenceManager: SharedPreferenceManager
) : Interceptor {

    private var isRedirectedToLogin: Boolean = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            val responseBody = response.peekBody(Long.MAX_VALUE).string()
            val jsonObject = JSONObject(responseBody)
            val errorMessage = jsonObject.getString("message").trim().lowercase()

            if (
                errorMessage == SessionUtils.sessionMsg1(context)
                || errorMessage == SessionUtils.sessionMsg2(context)
                || errorMessage == SessionUtils.sessionMsg3(context)
            ) {
                sharedPreferenceManager.saveAuthIdInPref(null)
                sharedPreferenceManager.saveAuthTokenInPref(null)
                sharedPreferenceManager.saveAuthModelInPref(null)
                sharedPreferenceManager.saveIsDriverStatus(null)

                if (!isRedirectedToLogin) {
                    isRedirectedToLogin = true // Set the flag to true to indicate redirection
                    val intent = Intent(context, SignInActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                    Handler(Looper.getMainLooper()).post {
                        Toast(context).showErrorMessage(
                            context,
                            errorMessage
                        )
                    }
                }
            }
        }

        return response
    }

}