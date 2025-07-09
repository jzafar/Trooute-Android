package com.travel.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.domain.usecase.trip.GetTripsUseCase
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
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?, fetchAll: Boolean
    ) {
        viewModelScope.launch {
            _getTripsState.emit(Resource.LOADING)
            _getTripsState.emit(useCase.invoke(
                fromLatitude = fromLatitude, fromLongitude = fromLongitude, departureDate = departureDate, fetchAll = fetchAll
            ))
        }
    }
}