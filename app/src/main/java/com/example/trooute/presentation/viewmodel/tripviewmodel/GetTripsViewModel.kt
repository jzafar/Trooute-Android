package com.example.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripsResponse
import com.example.trooute.domain.usecase.trip.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetTripsViewModel @Inject constructor(
    private val useCase: GetTripsUseCase
) : ViewModel() {
    private val _getTripsState = MutableStateFlow<Resource<GetTripsResponse>>(Resource.LOADING)
    val getTripsState: StateFlow<Resource<GetTripsResponse>> get() = _getTripsState

    fun getTrips(
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?
    ) {
        viewModelScope.launch {
            _getTripsState.emit(Resource.LOADING)
            _getTripsState.emit(useCase.invoke(
                fromLatitude = fromLatitude, fromLongitude = fromLongitude, departureDate = departureDate
            ))
        }
    }
}