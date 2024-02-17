package com.example.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.domain.usecase.trip.UpdatePickupStatusUseCase
import com.example.trooute.domain.usecase.trip.UpdateTripStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePickupStatusViewModel @Inject constructor(
    private val useCase: UpdatePickupStatusUseCase
) : ViewModel() {
    private val _updatePickupStatusState = MutableStateFlow<Resource<GetTripDetailsResponse>>(Resource.LOADING)
    val updateTripStatusState: StateFlow<Resource<GetTripDetailsResponse>> get() = _updatePickupStatusState

    fun updatePickupStatus(request: UpdatePickupStatusRequest) {
        viewModelScope.launch {
            _updatePickupStatusState.emit(Resource.LOADING)
            _updatePickupStatusState.emit(useCase.invoke(request = request))
        }
    }
}