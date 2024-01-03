package com.example.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.trooute.R
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Constants.EMAIL
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.auth.request.LoginRequest
import com.example.trooute.databinding.ActivitySignInBinding
import com.example.trooute.presentation.ui.main.MainActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.isEmailValid
import com.example.trooute.presentation.utils.isPasswordValid
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.authviewmodel.LoginViewModel
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val TAG = "SignInActivity"

    private lateinit var binding: ActivitySignInBinding

    private val loginViewModel: LoginViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        binding.apply {
            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@SignInActivity, ResetPasswordActivity::class.java))
            }

            btnSignIn.setOnClickListener {
                if (
                    isEmailValid(teEmailAddress)
                    && isPasswordValid(true, tePassword)
                ) {
                    loginViewModel.login(
                        LoginRequest(
                            email = teEmailAddress.text.toString(),
                            password = tePassword.text.toString(),
                        )
                    )

                    bindSignInObserver()
                }
            }

            tvSingUp.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }
        }

        // Back press handler
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }

        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )
    }

    private fun bindSignInObserver() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@SignInActivity).showErrorMessage(
                            this@SignInActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@SignInActivity).showSuccessMessage(
                            this@SignInActivity,
                            it.data.message.toString()
                        )

                        sharedPreferenceManager.saveAuthTokenInPref(it.data.token.toString())

                        if (it.data.message == "Verification Email sent.") {
                            startActivity(
                                Intent(
                                    this@SignInActivity,
                                    AuthVerificationActivity::class.java
                                ).putExtra(EMAIL, binding.teEmailAddress.text.toString())
                            )
                        } else {
                            Log.e(TAG, "bindSignInObserver: uID -> $TROOUTE_TOPIC${it.data.data?._id.toString()}")
                            pushNotificationViewModel.subscribeTopic("$TROOUTE_TOPIC${it.data.data?._id.toString()}")
                            bindSubscribeTopicObserver(it.data.data?._id.toString())
                        }

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

    private fun bindSubscribeTopicObserver(uID: String) {
        pushNotificationViewModel.topicState.onEach {
            when (it) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindSubscribeTopicObserver: Error -> ${it.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindSubscribeTopicObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindSubscribeTopicObserver: Success -> ${it.data}")
                    sharedPreferenceManager.saveAuthIdInPref(uID)
                    startActivity(
                        Intent(
                            this@SignInActivity,
                            MainActivity::class.java
                        )
                    )
                }
            }
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}