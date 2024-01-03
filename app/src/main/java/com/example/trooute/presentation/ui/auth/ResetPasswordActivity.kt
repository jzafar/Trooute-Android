package com.example.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.databinding.ActivityResetPasswordBinding
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.isEmailValid
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.authviewmodel.ForgotPasswordViewModel
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private val TAG = "ResetPasswordActivity"

    private lateinit var binding: ActivityResetPasswordBinding

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)

        binding.apply {
            btnSendLink.setOnClickListener {
                if (
                    isEmailValid(teEmailAddress)
                ) {
                    forgotPasswordViewModel.forgotPassword(
                        ForgotPasswordRequest(
                            email = teEmailAddress.text.toString(),
                        )
                    )

                    bindAuthObserver()
                }
            }
        }
    }

    private fun bindAuthObserver() {
        lifecycleScope.launch {
            forgotPasswordViewModel.forgotPasswordState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@ResetPasswordActivity).showErrorMessage(
                            this@ResetPasswordActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@ResetPasswordActivity).showSuccessMessage(
                            this@ResetPasswordActivity,
                            it.data.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: success : " + it.data)
                        startActivity(
                            Intent(
                                this@ResetPasswordActivity,
                                SignInActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}