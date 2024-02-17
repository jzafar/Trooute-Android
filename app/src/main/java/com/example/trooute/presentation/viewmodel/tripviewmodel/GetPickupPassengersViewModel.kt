package com.example.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.domain.usecase.trip.GetPickupPassengersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPickupPassengersViewModel @Inject constructor(
    private val useCase: GetPickupPassengersUseCase
) : ViewModel() {
    private val _getPickupState = MutableStateFlow<Resource<GetTripDetailsResponse>>(Resource.LOADING)
    val getPickupState: StateFlow<Resource<GetTripDetailsResponse>> get() = _getPickupState
    fun getPickUpStatus(tripsID: String) {
        viewModelScope.launch {
            _getPickupState.emit(Resource.LOADING)
            _getPickupState.emit(useCase.invoke(tripId = tripsID))
        }
    }
}