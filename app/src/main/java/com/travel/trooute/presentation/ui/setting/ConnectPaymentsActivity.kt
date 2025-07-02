package com.travel.trooute.presentation.ui.setting

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.connectPayments.ConnectPaypalRequest
import com.travel.trooute.databinding.ActivityConnectPaymentsBinding
import com.travel.trooute.presentation.ui.BaseActivity
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.PayPalAuthManager
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.connectPaymetns.ConnectPayPalViewModel
import com.travel.trooute.presentation.viewmodel.connectPaymetns.ConnectStripeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConnectPaymentsActivity :  BaseActivity() {
    private lateinit var binding: ActivityConnectPaymentsBinding
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var loader: Loader

    private var isPayPalConnected = false
    private var isStripeConnected = false
    private val connectPayPalViewModel: ConnectPayPalViewModel by viewModels()
    private val connectStripeViewModel: ConnectStripeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connect_payments)
        if (!sharedPreferenceManager.getAuthModelFromPref()?.stripeConnectedAccountId.isNullOrEmpty()) {
            isStripeConnected = true
        }
        if (!sharedPreferenceManager.getAuthModelFromPref()?.payPalEmail.isNullOrEmpty()) {
            isPayPalConnected = true
        }
        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = intent.getStringExtra("ToolBarTitle")
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }
        }
        updateUI()
    }

    private fun updateUI() {
        val paypalContainer = binding.paypalContainer
        paypalContainer.removeAllViews()
        if (isPayPalConnected) {
            addStatusRow(paypalContainer, getString(R.string.PayPal))
        } else {
            addButton(paypalContainer, getString(R.string.connect_paypal_account)) {
                loginWithPayPal()
            }
        }

        val stripeContainer = binding.stripeContainer
        stripeContainer.removeAllViews()
        if (isStripeConnected) {
            addStatusRow(stripeContainer, getString(R.string.stripe_account))
        } else {
            addButton(stripeContainer, getString(R.string.connect_stripe_account)) {
                connectStripeAccount()
            }
        }
    }

    private fun addStatusRow(container: LinearLayout, label: String) {
        val textView = TextView(this).apply {
            text = label
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
        }
        val circle = View(this).apply {
            setBackgroundResource(R.drawable.ic_green_circle)
            layoutParams = LinearLayout.LayoutParams(40, 40)
        }
        container.addView(textView)
        container.addView(circle)
    }

    private fun addButton(container: LinearLayout, text: String, onClick: () -> Unit) {
        val button = Button(this).apply {
            this.text = text
            setOnClickListener { onClick() }
        }
        container.addView(button)
    }

    private fun loginWithPayPal() {
        val authManager = PayPalAuthManager(this)
        authManager.login { code ->
            if (code != null) {
                updatePayPalOnServer(code)
            } else {
                runOnUiThread {
                    Toast(this).showErrorMessage(
                        this, getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun updatePayPalOnServer(code: String) {
        connectPayPalViewModel.connectPaypal(ConnectPaypalRequest(code))
        bindConnectPaymentsObserver()
    }

    private fun connectStripeAccount() {
        connectStripeViewModel.connectStripe()
        bindConnectStripe()
    }

    private fun bindConnectPaymentsObserver() {
        lifecycleScope.launch {
            connectPayPalViewModel.connectPaymentsState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@ConnectPaymentsActivity).showErrorMessage(
                            this@ConnectPaymentsActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindConnectPaymentsObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@ConnectPaymentsActivity).showSuccessMessage(
                            this@ConnectPaymentsActivity,
                            it.data.message.toString()
                        )
                        isPayPalConnected = true
                        it.data.data?.let { user ->
                            sharedPreferenceManager.saveIsDriverStatus(user.isApprovedDriver)
                            sharedPreferenceManager.saveDriverMode(user.driverMode)
                            sharedPreferenceManager.saveAuthModelInPref(user)
                        }
                    }
                }
            }
        }
    }

    private fun bindConnectStripe() {
        lifecycleScope.launch {
            connectStripeViewModel.connectStripeState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@ConnectPaymentsActivity).showErrorMessage(
                            this@ConnectPaymentsActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindConnectPaymentsObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@ConnectPaymentsActivity).showSuccessMessage(
                            this@ConnectPaymentsActivity,
                            it.data.message.toString()
                        )
                        isPayPalConnected = true
                        updateUI()
                        it.data.data?.let { user ->
                            sharedPreferenceManager.saveIsDriverStatus(user.isApprovedDriver)
                            sharedPreferenceManager.saveDriverMode(user.driverMode)
                            sharedPreferenceManager.saveAuthModelInPref(user)
                        }
                    }
                }
            }
        }
    }
}