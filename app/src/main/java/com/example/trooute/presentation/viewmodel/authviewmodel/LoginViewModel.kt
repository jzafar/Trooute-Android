package com.example.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.LoginRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: SignInUseCase) : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val loginState: StateFlow<Resource<AuthResponse>> get() = _loginState

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.emit(Resource.LOADING)
            _loginState.emit(useCase.invoke(request))
        }
    }
}