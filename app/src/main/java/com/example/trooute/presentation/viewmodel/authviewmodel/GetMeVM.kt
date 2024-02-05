package com.example.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.usecase.auth.GetMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class GetMeVM @Inject constructor(private val useCase: GetMeUseCase) : ViewModel() {
    private val _getMeState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val getMeState: StateFlow<Resource<AuthResponse>> get() = _getMeState

    fun getMe(){
        viewModelScope.launch {
            _getMeState.value = Resource.LOADING
            _getMeState.value = useCase.invoke()
        }
    }
}