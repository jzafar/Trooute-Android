package com.travel.trooute.core.interceptor

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.travel.trooute.R
import com.travel.trooute.core.connectivity_monitor.NetworkStatusTracker
import com.travel.trooute.presentation.utils.showErrorMessage
import okhttp3.Interceptor
import okhttp3.Response

class ConnectionInterceptor(
    private val context: Context,
    private val networkStatusTracker: NetworkStatusTracker
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkStatusTracker.isOnline()) {
            Handler(Looper.getMainLooper()).post {
                Toast(context).showErrorMessage(
                    context,
                    context.getString(R.string.network_message)
                )
            }
        }
        return chain.proceed(chain.request())
    }
}