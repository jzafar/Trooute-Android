package com.example.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.domain.usecase.trip.CreateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTripViewModel @Inject constructor(
    private val useCase: CreateTripUseCase
) : ViewModel() {
    private val _createTripState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val createTripState: StateFlow<Resource<BaseResponse>> get() = _createTripState

    fun createTrip(request: CreateTripRequest) {
        viewModelScope.launch {
            _createTripState.emit(Resource.LOADING)
            _createTripState.emit(useCase.invoke(request = request))
        }
    }
}