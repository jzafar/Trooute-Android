package com.travel.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.travel.trooute.databinding.ActivityUpdatePasswordBinding
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.isConfirmPasswordValid
import com.travel.trooute.presentation.utils.isPasswordValid
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.UpdateMyPasswordVM
import com.google.android.material.internal.ViewUtils
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdatePasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding

    private val viewModel: UpdateMyPasswordVM by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_password)

        binding.apply {
            btnUpdate.setOnClickListener {
                if (
                    isPasswordValid(false, teNewPassword) &&
                    isPasswordValid(false, teCurrentPassword) &&
                    isConfirmPasswordValid(teNewPassword, teRetypePassword)
                ) {
                    viewModel.updateMyPassword(
                        UpdateMyPasswordRequest(
                            password = teNewPassword.text.toString(),
                            passwordConfirm = teRetypePassword.text.toString(),
                            passwordCurrent = teCurrentPassword.text.toString(),
                        )
                    )

                    bindMyPasswordObserver()
                }
            }
        }
    }

    private fun bindMyPasswordObserver() {
        lifecycleScope.launch {
            viewModel.updateMyPasswordState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@UpdatePasswordActivity).showErrorMessage(
                            this@UpdatePasswordActivity,
                            it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@UpdatePasswordActivity).showSuccessMessage(
                            this@UpdatePasswordActivity,
                            getString(R.string.password_updated_success)
                        )
                        sharedPreferenceManager.saveAuthTokenInPref(it.data.token.toString())
                        it.data.data?.let { user ->
                            sharedPreferenceManager.saveIsDriverStatus(user.isApprovedDriver)
                            sharedPreferenceManager.saveDriverMode(user.driverMode)
                            sharedPreferenceManager.saveAuthModelInPref(user)
                        }
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