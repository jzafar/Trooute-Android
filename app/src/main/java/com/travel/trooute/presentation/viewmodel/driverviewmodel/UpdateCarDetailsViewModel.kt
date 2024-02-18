package com.travel.trooute.presentation.viewmodel.driverviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse
import com.travel.trooute.domain.usecase.driver.UpdateCarInfoRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateCarDetailsViewModel @Inject constructor(
    private val useCase: UpdateCarInfoRequestUseCase
) : ViewModel() {
    private val _updateCarInfoDetailsState =
        MutableStateFlow<Resource<UpdateCarInfoRequestsResponse>>(Resource.LOADING)
    val updateCarInfoDetailsState: StateFlow<Resource<UpdateCarInfoRequestsResponse>> get() = _updateCarInfoDetailsState

    fun updateCarDetails(request: UploadDriverDetailsRequest) {
        viewModelScope.launch {
            _updateCarInfoDetailsState.value = Resource.LOADING
            _updateCarInfoDetailsState.emit(useCase.invoke(request = request))
        }
    }
}