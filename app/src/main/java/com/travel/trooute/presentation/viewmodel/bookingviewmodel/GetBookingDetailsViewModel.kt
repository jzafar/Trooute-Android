package com.travel.trooute.presentation.viewmodel.bookingviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.travel.trooute.domain.usecase.booking.GetBookingDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetBookingDetailsViewModel @Inject constructor(
    private val useCase: GetBookingDetailsUseCase
) : ViewModel() {
    private val _getBookingDetailsState = MutableStateFlow<Resource<GetBookingDetailResponse>>(Resource.LOADING)
    val getBookingDetailsState: StateFlow<Resource<GetBookingDetailResponse>> get() = _getBookingDetailsState

    fun getBookingDetails(bookingId: String?) {
        viewModelScope.launch {
            _getBookingDetailsState.value = Resource.LOADING
            _getBookingDetailsState.emit(useCase.invoke(bookingId = bookingId))
        }
    }
}