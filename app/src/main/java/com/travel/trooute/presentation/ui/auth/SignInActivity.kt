package com.travel.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.EMAIL
import com.travel.trooute.core.util.Constants.TROOUTE_TOPIC
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.auth.request.LoginRequest
import com.travel.trooute.databinding.ActivitySignInBinding
import com.travel.trooute.presentation.ui.main.MainActivity
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isEmailValid
import com.travel.trooute.presentation.utils.isPasswordValid
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.LoginViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Base64
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val TAG = "SignInActivity"
    private var isBiometricSignIn = false

    private lateinit var binding: ActivitySignInBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

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
        initializeBiometricsFlow()


        binding.apply {
            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@SignInActivity, ResetPasswordActivity::class.java))
            }

            btnSignIn.setOnClickListener {
                if (
                    isEmailValid(teEmailAddress)
                    && isPasswordValid(true, tePassword)
                ) {
                    isBiometricSignIn = false

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

            biometricLoginButton.setOnClickListener {
                isBiometricSignIn = true
                biometricPrompt.authenticate(promptInfo)
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
//                        Toast(this@SignInActivity).showSuccessMessage(
//                            this@SignInActivity,
//                            it.data.message.toString()
//                        )

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
                    loader.cancel()
                }

                Resource.LOADING -> {
                    loader.show()
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
                    loader.cancel()
                }
            }
        }.launchIn(lifecycleScope)

    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }

    private fun initializeBiometricsFlow() {
        if (BiometricManager.from(this)
                .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            binding.biometricLoginButton.setVisibility(View.GONE)
        } else {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast(this@SignInActivity).showErrorMessage(
                            this@SignInActivity,
                            getString(R.string.authentication_error)
                        )
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val prefBioInfo = sharedPreferenceManager.getBiometricInfo()
                        val decodedInfo = String(Base64.getDecoder().decode(prefBioInfo))
                        val biometricInfo = decodedInfo.split(":")
                        loginViewModel.login(
                            LoginRequest(
                                email = biometricInfo[0].toString(),
                                password = biometricInfo[1].toString(),
                            )
                        )
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast(this@SignInActivity).showErrorMessage(
                            this@SignInActivity,
                            getString(R.string.authentication_error)
                        )
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.bio_login))
                .setSubtitle(getString(R.string.login_with_bio))
                .setNegativeButtonText(getString(R.string.use_password))
                .build()
        }
    }

}