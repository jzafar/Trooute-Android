package com.example.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.usecase.auth.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val useCase: ForgotPasswordUseCase) : ViewModel() {
    private val _forgotPasswordState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val forgotPasswordState: StateFlow<Resource<AuthResponse>> get() = _forgotPasswordState

    fun forgotPassword(request: ForgotPasswordRequest) {
        viewModelScope.launch {
            _forgotPasswordState.value = Resource.LOADING
            _forgotPasswordState.value = useCase.invoke(request)
        }
    }
}