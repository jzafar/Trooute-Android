package com.travel.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.domain.usecase.trip.DriverTripsHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverTripsHistoryViewModel @Inject constructor(
    private val useCase: DriverTripsHistoryUseCase
) : ViewModel() {
    private val _driverTripsHistoryState =
        MutableStateFlow<Resource<GetTripsResponse>>(Resource.LOADING)
    val driverTripsHistoryState: StateFlow<Resource<GetTripsResponse>> get() = _driverTripsHistoryState

    fun tripsHistory() {
        viewModelScope.launch {
            _driverTripsHistoryState.emit(Resource.LOADING)
            _driverTripsHistoryState.emit(useCase.invoke())
        }
    }
}