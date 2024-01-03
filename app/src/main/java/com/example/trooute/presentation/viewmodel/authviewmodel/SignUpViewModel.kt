package com.example.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val useCase: SignUpUseCase) : ViewModel() {
    private val _signUpState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val signUpState: StateFlow<Resource<AuthResponse>> get() = _signUpState

    fun signUp(request: MultipartBody){
        viewModelScope.launch {
            _signUpState.value = Resource.LOADING
            _signUpState.value = useCase.invoke(request)
        }
    }
}