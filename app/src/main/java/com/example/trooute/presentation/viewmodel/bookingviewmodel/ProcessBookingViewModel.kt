package com.example.trooute.presentation.viewmodel.bookingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.usecase.booking.ApproveBookingUseCase
import com.example.trooute.domain.usecase.booking.CancelBookingUseCase
import com.example.trooute.domain.usecase.booking.CompleteBookingUseCase
import com.example.trooute.domain.usecase.booking.ConfirmBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessBookingViewModel @Inject constructor(
    private val approveBookingUseCase: ApproveBookingUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val completeBookingUseCase: CompleteBookingUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase
) : ViewModel() {
    private val _processBookingState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val processBookingState: StateFlow<Resource<BaseResponse>> get() = _processBookingState

    fun approveBooking(bookingId: String?) {
        viewModelScope.launch {
            _processBookingState.value = Resource.LOADING
            _processBookingState.emit(approveBookingUseCase.invoke(bookingId = bookingId))
        }
    }

    fun cancelBooking(bookingId: String?) {
        viewModelScope.launch {
            _processBookingState.value = Resource.LOADING
            _processBookingState.emit(cancelBookingUseCase.invoke(bookingId = bookingId))
        }
    }

    fun completeBooking(bookingId: String?) {
        viewModelScope.launch {
            _processBookingState.value = Resource.LOADING
            _processBookingState.emit(completeBookingUseCase.invoke(bookingId = bookingId))
        }
    }

    fun confirmBooking(bookingId: String?) {
        viewModelScope.launch {
            _processBookingState.value = Resource.LOADING
            _processBookingState.emit(confirmBookingUseCase.invoke(bookingId = bookingId))
        }
    }
}