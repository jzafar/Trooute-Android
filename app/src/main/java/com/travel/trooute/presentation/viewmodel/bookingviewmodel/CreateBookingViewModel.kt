package com.travel.trooute.presentation.viewmodel.bookingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.usecase.booking.CreateBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBookingViewModel @Inject constructor(
    private val useCase: CreateBookingUseCase
) : ViewModel() {
    private val _createBookingState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val createBookingState: StateFlow<Resource<BaseResponse>> get() = _createBookingState

    fun createBooking(request: CreateBookingRequest) {
        viewModelScope.launch {
            _createBookingState.emit(Resource.LOADING)
            _createBookingState.emit(useCase.invoke(request = request))
        }
    }
}