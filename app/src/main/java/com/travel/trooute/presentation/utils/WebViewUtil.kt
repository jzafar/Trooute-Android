package com.travel.trooute.presentation.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.TROOUTE_TOPIC
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.notification.NotificationRequest
import com.travel.trooute.presentation.ui.main.MainActivity
import com.travel.trooute.presentation.viewmodel.bookingviewmodel.PaymentSuccessViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebViewUtil(
    private val activity: Activity,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val paymentSuccessViewModel: PaymentSuccessViewModel,
    private val pushNotificationViewModel: PushNotificationViewModel,
    private val lifecycleScope: LifecycleCoroutineScope,
    private var loader: Loader
) : WebViewClient() {

    private val TAG = "WebViewUtil"

    private var isPageError = false

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        isPageError = false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        if (url.contains("payment-success") || url.contains("paypal-success")) {
            val userId = sharedPreferenceManager.getAuthModelFromPref()?._id
            val updatedUrl = url.replace("http://localhost:4000", "").let {
                val separator = if (it.contains("?")) "&" else "?"
                "$it${separator}userId=$userId"
            }

            // Prevent WebView from loading this URL
            paymentSuccessViewModel.paymentSuccess(updatedUrl)
            bindPaymentSuccessObservers()
            return true // ← prevent loading the URL in WebView
        }

        if (url.contains("payment-failed") || url.contains("paypal-cancel")) {
            /*
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("paymentSuccess", false)
            activity.startActivity(intent)
             */
            val resultIntent = Intent().apply {
                putExtra("paymentSuccess", false)
            }
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
            return true
        }
        return false
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
        /*
        if (url?.contains("payment-success") == true || url?.contains("paypal-success") == true) {
            val userId =  sharedPreferenceManager.getAuthModelFromPref()?._id
            val updatedUrl = url.replace("http://localhost:4000", "").let {
                val separator = if (it.contains("?")) "&" else "?"
                "$it${separator}userId=$userId"
            }
            paymentSuccessViewModel.paymentSuccess(updatedUrl)
            bindPaymentSuccessObservers()
        }

        if (url?.contains("payment-failed") == true || url?.contains("paypal-cancel") == true) {
            /*
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("paymentSuccess", false)
            activity.startActivity(intent)
             */
            val resultIntent = Intent().apply {
                putExtra("paymentSuccess", false)
            }
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }
         */
    }

    private fun bindPaymentSuccessObservers() {
        lifecycleScope.launch {
            paymentSuccessViewModel.paymentSuccessState.collect {
                loader.hide()
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindPaymentSuccessObservers: Error " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
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

    private fun sendNotification() {
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

    private fun bindSendMessageNotificationObserver() {
        pushNotificationViewModel.sendNotificationState.onEach { state ->
            when (state) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Error -> ${state.message}")
                    val resultIntent = Intent().apply {
                        putExtra("paymentSuccess", true)
                    }
                    activity.setResult(Activity.RESULT_OK, resultIntent)
                    activity.finish()
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Success -> ${state.data}")
                    /*
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("paymentSuccess", true)
                    activity.startActivity(intent)
                    */
                    val resultIntent = Intent().apply {
                        putExtra("paymentSuccess", true)
                    }
                    activity.setResult(Activity.RESULT_OK, resultIntent)
                    activity.finish()
                }
            }
        }.launchIn(lifecycleScope)
    }
}