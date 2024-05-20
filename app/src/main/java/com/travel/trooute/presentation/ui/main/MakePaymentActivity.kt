package com.travel.trooute.presentation.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.databinding.ActivityMakePaymentBinding
import com.travel.trooute.presentation.ui.BaseActivity
import com.travel.trooute.presentation.utils.WebViewUtil
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.viewmodel.bookingviewmodel.PaymentSuccessViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MakePaymentActivity : BaseActivity() {

    private val TAG = "MakePaymentActivity"

    private lateinit var binding: ActivityMakePaymentBinding
    private lateinit var webViewUtil: WebViewUtil

    private val paymentSuccessViewModel: PaymentSuccessViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_payment)

//        webViewUtil = WebViewUtil(this) {
//            paymentSuccessViewModel.paymentSuccess(it)
//            bindPaymentSuccessObservers()
//        }

        webViewUtil = WebViewUtil(
            this,
            sharedPreferenceManager,
            paymentSuccessViewModel,
            pushNotificationViewModel,
            lifecycleScope
        )

        binding.apply {
            with(webView) {
                // WebViewClient allows you to handle
                // onPageFinished and override Url loading.
                webViewClient = webViewUtil

                // this will load the url of the website
                loadUrl(intent.getStringExtra("PaymentIntegrationUrl").toString())

                // this will enable the javascript settings, it can also allow xss vulnerabilities
                settings.javaScriptEnabled = true

                // if you want to enable zoom feature
                settings.setSupportZoom(true)
            }
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
                    }
                }
            }
        }
    }

    @SuppressLint("GestureBackNavigation")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}