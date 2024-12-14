package com.travel.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.LoginRequest
import com.travel.trooute.data.model.auth.request.LogoutRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.usecase.auth.SignInUseCase
import com.travel.trooute.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val useCase: SignOutUseCase) : ViewModel() {
    private val _logoutState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val logoutState: StateFlow<Resource<BaseResponse>> get() = _logoutState

    fun logout(request: LogoutRequest) {
        viewModelScope.launch {
            _logoutState.emit(Resource.LOADING)
            _logoutState.emit(useCase.invoke(request))
        }
    }
}