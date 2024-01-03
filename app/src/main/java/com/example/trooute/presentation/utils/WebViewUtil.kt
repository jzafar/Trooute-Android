package com.example.trooute.presentation.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.presentation.ui.main.MainActivity
import com.example.trooute.presentation.viewmodel.bookingviewmodel.PaymentSuccessViewModel
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WebViewUtil(
    private val activity: Activity,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val paymentSuccessViewModel: PaymentSuccessViewModel,
    private val pushNotificationViewModel: PushNotificationViewModel,
    private val lifecycleScope: LifecycleCoroutineScope
) : WebViewClient() {

    private val TAG = "WebViewUtil"

    private var isPageError = false

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        isPageError = false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        view?.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (isPageError) {
            view?.isVisible = false
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        Log.e(TAG, "onReceivedError: $error")
        isPageError = true
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        Log.e(TAG, "onLoadResource: url -> $url")

        // Check if the URL contains "payment-success"
        if (url?.contains("payment-success") == true) {
            paymentSuccessViewModel.paymentSuccess(url.replace("http://localhost:4000", ""))
            bindPaymentSuccessObservers()
        }
    }

    private fun bindPaymentSuccessObservers() {
        lifecycleScope.launch {
            paymentSuccessViewModel.paymentSuccessState.collect {
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindPaymentSuccessObservers: Error " + it.message.toString())
                    }

                    Resource.LOADING -> {

                    }

                    is Resource.SUCCESS -> {
                        Log.e(TAG, "bindPaymentSuccessObservers: success " + it.data)
                        Toast(activity).showSuccessMessage(activity, it.data.message.toString())

                        pushNotificationViewModel.sendMessageNotification(
                            NotificationRequest(
                                notification = NotificationRequest.Notification(
                                    title = Constants.MAKE_PAYMENT_TITLE,
                                    body = "${Constants.MAKE_PAYMENT_BODY} ${sharedPreferenceManager.getAuthModelFromPref()?.name}.",
                                    mutable_content = Constants.MUTABLE_CONTENT,
                                    sound = Constants.TONE
                                ),
                                to = "${Constants.TOPIC}${TROOUTE_TOPIC}${sharedPreferenceManager.getMakePaymentUserIdFromPref()}"
                            )
                        )
                        bindSendMessageNotificationObserver()
                    }
                }
            }
        }
    }

    private fun bindSendMessageNotificationObserver() {
        pushNotificationViewModel.sendNotificationState.onEach { state ->
            when (state) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Error -> ${state.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Success -> ${state.data}")

                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("paymentSuccess", true)
                    activity.startActivity(intent)
                }
            }
        }.launchIn(lifecycleScope)
    }
}