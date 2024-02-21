package com.travel.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.UpdateDeviceIdRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.auth.response.DeviceIdResponse
import com.travel.trooute.domain.usecase.auth.GetMeUseCase
import com.travel.trooute.domain.usecase.auth.UpdateDeviceIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateDeviceIdVM @Inject constructor(private val useCase: UpdateDeviceIdUseCase) : ViewModel() {
    private val _updateDeviceId = MutableStateFlow<Resource<DeviceIdResponse>>(Resource.LOADING)
    val updateDeviceId: StateFlow<Resource<DeviceIdResponse>> get() = _updateDeviceId

    fun updateDeviceId(request: UpdateDeviceIdRequest){
        viewModelScope.launch {
            _updateDeviceId.value = Resource.LOADING
            _updateDeviceId.value = useCase.invoke(request = request)
        }
    }
}