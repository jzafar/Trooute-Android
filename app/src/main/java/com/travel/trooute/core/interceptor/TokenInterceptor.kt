package com.travel.trooute.core.interceptor

import com.travel.trooute.core.util.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val sharedPreferenceManager: SharedPreferenceManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        // Add the Authorization header with the user token
        val token = sharedPreferenceManager.getAuthTokenFromPref().toString()
        if (token.isNotEmpty()) {
            val authToken = "Bearer $token"
            request.addHeader("Authorization", authToken)
        }
        return chain.proceed(request.build())
    }
}