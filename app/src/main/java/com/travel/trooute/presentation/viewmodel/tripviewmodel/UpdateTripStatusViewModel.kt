package com.travel.trooute.presentation.viewmodel.tripviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.usecase.trip.UpdateTripStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateTripStatusViewModel @Inject constructor(
    private val useCase: UpdateTripStatusUseCase
) : ViewModel() {
    private val _updateTripStatusState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val updateTripStatusState: StateFlow<Resource<BaseResponse>> get() = _updateTripStatusState

    fun updateTripStatus(tripsID: String, status: String) {
        viewModelScope.launch {
            _updateTripStatusState.emit(Resource.LOADING)
            _updateTripStatusState.emit(useCase.invoke(tripId = tripsID, status = status))
        }
    }
}