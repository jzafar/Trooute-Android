package com.example.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.domain.usecase.trip.GetTripDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetTripDetailsViewModel @Inject constructor(
    private val useCase: GetTripDetailsUseCase
) : ViewModel() {
    private val _getTripDetailsState = MutableStateFlow<Resource<GetTripDetailsResponse>>(Resource.LOADING)
    val getTripDetailsState: StateFlow<Resource<GetTripDetailsResponse>> get() = _getTripDetailsState

    fun getTrips(tripsID: String) {
        viewModelScope.launch {
            _getTripDetailsState.emit(Resource.LOADING)
            _getTripDetailsState.emit(useCase.invoke(tripId = tripsID))
        }
    }
}