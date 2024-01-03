package com.example.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.usecase.auth.ResendEmailVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResendEmailVerificationCodeViewModel @Inject constructor(
    private val useCase: ResendEmailVerificationCodeUseCase
) : ViewModel() {
    private val _resendVerificationCodeState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val resendVerificationCodeState: StateFlow<Resource<AuthResponse>> get() = _resendVerificationCodeState

    fun resendEmailVerificationCode(request: ResendVerificationCodeRequest) {
        viewModelScope.launch {
            _resendVerificationCodeState.emit(Resource.LOADING)
            _resendVerificationCodeState.emit(useCase.invoke(request))
        }
    }
}