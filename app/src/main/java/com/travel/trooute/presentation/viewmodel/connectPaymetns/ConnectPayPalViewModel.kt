package com.travel.trooute.presentation.viewmodel.connectPaymetns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.connectPayments.ConnectPaypalRequest
import com.travel.trooute.domain.usecase.connectPayments.ConnectPayPalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectPayPalViewModel @Inject constructor(
    private val payPalUseCase: ConnectPayPalUseCase
) : ViewModel() {
    private val _connectPaymentsState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val connectPaymentsState: StateFlow<Resource<AuthResponse>> get() = _connectPaymentsState

    fun connectPaypal(request: ConnectPaypalRequest) {
        viewModelScope.launch {
            _connectPaymentsState.value = Resource.LOADING
            _connectPaymentsState.emit(payPalUseCase.invoke(request = request))
        }
    }
}