package com.travel.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.EmailVerificationRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.usecase.auth.EmailVerificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val useCase: EmailVerificationUseCase
) : ViewModel() {
    private val _verificationState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val verificationState: StateFlow<Resource<AuthResponse>> get() = _verificationState

    fun emailVerification(request: EmailVerificationRequest) {
        viewModelScope.launch {
            _verificationState.value = Resource.LOADING
            _verificationState.value = useCase.invoke(request)
        }
    }
}