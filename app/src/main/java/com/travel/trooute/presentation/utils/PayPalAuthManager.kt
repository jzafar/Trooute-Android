package com.travel.trooute.presentation.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.travel.trooute.core.util.Constants
import java.util.UUID

class PayPalAuthManager(private val activity: Activity) {

    fun login(completion: (String?) -> Unit) {
        val authUri = buildAuthUri()

        try {
            // Simple CustomTabsIntent without service connection
            val customTabsIntent = CustomTabsIntent.Builder()
                .build()
                .apply {
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

            customTabsIntent.launchUrl(activity, authUri)

            // Set up callback handler
            PayPalAuthResultHandler.setCallback { redirectUri ->
                val code = Uri.parse(redirectUri).getQueryParameter("code")
                completion(code)
            }

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to open browser", Toast.LENGTH_SHORT).show()
            completion(null)
        }
    }

    private fun buildAuthUri(): Uri {
        return Uri.parse(PayPalService.payPalURL)
            .buildUpon()
            .appendQueryParameter("client_id", PayPalService.clientID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", "openid email")
            .appendQueryParameter("redirect_uri", Constants.PAYPAL_REDIRECT_URI)
            .appendQueryParameter("nonce", UUID.randomUUID().toString())
            .build()
    }
}
object PayPalAuthResultHandler {
    private var callback: ((String) -> Unit)? = null
    fun setCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    fun handleRedirect(uri: String) {
        Log.d("PayPalAuth", "Received redirect URI: $uri")
        callback?.invoke(uri)
        callback = null
    }
}

object PayPalService {

    val isSandbox: Boolean
        get() = true

    val clientID: String
        get() = if (isSandbox) Constants.PAYPAL_CLIENT_ID_SANDBOX else Constants.PAYPAL_CLIENT_ID

    val secret: String
        get() = if (isSandbox) Constants.PAYPAL_SECRET_SANDBOX else Constants.PAYPAL_SECRET

    val payPalURL: String
        get() = if (isSandbox)
            "https://www.sandbox.paypal.com/signin/authorize"
        else
            "https://www.paypal.com/signin/authorize"
}
