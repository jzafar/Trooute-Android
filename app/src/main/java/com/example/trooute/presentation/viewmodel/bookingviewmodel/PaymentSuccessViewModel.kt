package com.example.trooute.presentation.viewmodel.bookingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.usecase.booking.PaymentSuccessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor(
    private val useCase: PaymentSuccessUseCase
) : ViewModel() {
    private val _paymentSuccessState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val paymentSuccessState = _paymentSuccessState.asStateFlow()

    fun paymentSuccess(url: String?) {
        viewModelScope.launch {
            _paymentSuccessState.emit(useCase.invoke(url))
        }
    }
}