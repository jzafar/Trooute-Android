package com.example.trooute.core.connectivity_monitor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkStatusTracker @Inject constructor(@ApplicationContext private val context: Context) {
    private val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    // This function is for connectivity interceptor
    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    fun isOnline(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager
                .getNetworkCapabilities(networkCapabilities) ?: return false
            activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val netInfo = connectivityManager.activeNetworkInfo
            netInfo?.isConnectedOrConnecting == true
        }
    }
}