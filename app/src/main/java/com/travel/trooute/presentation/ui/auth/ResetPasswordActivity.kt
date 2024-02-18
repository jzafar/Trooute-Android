package com.travel.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.ForgotPasswordRequest
import com.travel.trooute.databinding.ActivityResetPasswordBinding
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isEmailValid
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.ForgotPasswordViewModel
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