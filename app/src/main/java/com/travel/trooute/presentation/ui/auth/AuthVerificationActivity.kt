package com.travel.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.EMAIL
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.EmailVerificationRequest
import com.travel.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.travel.trooute.databinding.ActivityAuthVerificationBinding
import com.travel.trooute.presentation.utils.GenericKeyEvent
import com.travel.trooute.presentation.utils.GenericTextWatcher
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.EmailVerificationViewModel
import com.travel.trooute.presentation.viewmodel.authviewmodel.ResendEmailVerificationCodeViewModel
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthVerificationActivity : AppCompatActivity() {

    private val TAG = "AuthVerificationActivity"

    private lateinit var binding: ActivityAuthVerificationBinding

    private var countDownTimer: CountDownTimer? = null
    private var verificationEmail = ""

    private val emailVerificationViewModel: EmailVerificationViewModel by viewModels()
    private val resendEmailVerificationCodeViewModel: ResendEmailVerificationCodeViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_verification)
        verificationEmail = intent.getStringExtra(EMAIL).toString()

        otpCountDownTimer()

        binding.apply {
            tvVerificationDesc.text =
                "Please enter the verification code sent to your $verificationEmail email address"

            //GenericTextWatcher here works only for moving to next EditText when a number is entered
            //first parameter is the current EditText and second parameter is next EditText
            teCode1.addTextChangedListener(GenericTextWatcher(teCode1, teCode2))
            teCode2.addTextChangedListener(GenericTextWatcher(teCode2, teCode3))
            teCode3.addTextChangedListener(GenericTextWatcher(teCode3, teCode4))
            teCode4.addTextChangedListener(GenericTextWatcher(teCode4, null))

            //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
            //first parameter is the current EditText and second parameter is previous EditText
            teCode1.setOnKeyListener(GenericKeyEvent(teCode1, null))
            teCode2.setOnKeyListener(GenericKeyEvent(teCode2, teCode1))
            teCode3.setOnKeyListener(GenericKeyEvent(teCode3, teCode2))
            teCode4.setOnKeyListener(GenericKeyEvent(teCode4, teCode3))

            tvResend.setOnClickListener {
                resendEmailVerificationCodeViewModel.resendEmailVerificationCode(
                    ResendVerificationCodeRequest(
                        email = verificationEmail
                    )
                )

                bindResendEmailVerificationCodeObserver()
            }

            btnConfirm.setOnClickListener {
                val otp = teCode1.text.toString() +
                        teCode2.text.toString() +
                        teCode3.text.toString() +
                        teCode4.text.toString()

                if (
                    isFieldValid(teCode1, "Code")
                    && isFieldValid(teCode2, "Code")
                    && isFieldValid(teCode3, "Code")
                    && isFieldValid(teCode4, "Code")
                ) {
                    emailVerificationViewModel.emailVerification(
                        EmailVerificationRequest(
                            OTP = otp
                        )
                    )

                    bindEmailVerificationObserver()
                }
            }
        }
    }

    private fun bindResendEmailVerificationCodeObserver() {
        lifecycleScope.launch {
            resendEmailVerificationCodeViewModel.resendVerificationCodeState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@AuthVerificationActivity).showErrorMessage(
                            this@AuthVerificationActivity,
                            it.message.toString()
                        )
                        Log.e(
                            TAG,
                            "bindResendEmailVerificationCodeObserver: Error: " + it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@AuthVerificationActivity).showSuccessMessage(
                            this@AuthVerificationActivity,
                            it.data.message.toString()
                        )
                        Log.e(
                            TAG,
                            "bindResendEmailVerificationCodeObserver: success : " + it.data
                        )
                        otpCountDownTimer()
                    }
                }
            }
        }
    }

    private fun bindEmailVerificationObserver() {
        lifecycleScope.launch {
            emailVerificationViewModel.verificationState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@AuthVerificationActivity).showErrorMessage(
                            this@AuthVerificationActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        Log.e(TAG, "bindAuthObserver: loading...")
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@AuthVerificationActivity).showSuccessMessage(
                            this@AuthVerificationActivity,
                            it.data.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: success : " + it.data)

                        startActivity(
                            Intent(
                                this@AuthVerificationActivity,
                                SignInActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun otpCountDownTimer() {
        binding.tvResend.isEnabled = false // Disable the button during countdown
        countDownTimer?.cancel() // Cancel any existing timer before starting a new one
        countDownTimer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvExpiredAfter.isVisible = true
                binding.tvExpiredAfter.text = "Expired after ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.tvExpiredAfter.isVisible = false
                // Enable the button after countdown finishes
                binding.tvResend.isEnabled = true
            }
        }
        countDownTimer?.start()
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}