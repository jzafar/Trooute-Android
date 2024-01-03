package com.example.trooute.presentation.viewmodel.bookingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.bookings.response.GetBookingsResponse
import com.example.trooute.domain.usecase.booking.GetBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetBookingViewModel @Inject constructor(
    private val useCase: GetBookingsUseCase
) : ViewModel() {
    private val _getBookingState = MutableStateFlow<Resource<GetBookingsResponse>>(Resource.LOADING)
    val getBookingState: StateFlow<Resource<GetBookingsResponse>> get() = _getBookingState

    fun getBooking() {
        viewModelScope.launch {
            _getBookingState.value = Resource.LOADING
            _getBookingState.emit(useCase.invoke())
        }
    }
}