package com.travel.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.usecase.auth.UpdateMyPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateMyPasswordVM @Inject constructor(
    private val useCase: UpdateMyPasswordUseCase
) : ViewModel() {
    private val _updateMyPasswordState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val updateMyPasswordState: StateFlow<Resource<AuthResponse>> get() = _updateMyPasswordState

    fun updateMyPassword(request: UpdateMyPasswordRequest) {
        viewModelScope.launch {
            _updateMyPasswordState.value = Resource.LOADING
            _updateMyPasswordState.value = useCase.invoke(request)
        }
    }
}