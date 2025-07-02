package com.travel.trooute.presentation.viewmodel.connectPaymetns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.usecase.connectPayments.ConnectStripeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectStripeViewModel @Inject constructor(
    private val stripeUseCase: ConnectStripeUseCase
) : ViewModel() {
    private val _connectStripeState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val connectStripeState: StateFlow<Resource<AuthResponse>> get() = _connectStripeState

    fun connectStripe() {
        viewModelScope.launch {
            _connectStripeState.value = Resource.LOADING
            _connectStripeState.emit(stripeUseCase.invoke())
        }
    }
}