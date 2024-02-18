package com.travel.trooute.presentation.viewmodel.driverviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.usecase.driver.ApproveDriverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproveDriverViewModel @Inject constructor(
    private val useCase: ApproveDriverUseCase
) : ViewModel() {
    private val _approveDriverState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val approveDriverState: StateFlow<Resource<BaseResponse>> get() = _approveDriverState

    fun approveDriverDetails(driverId: String?) {
        viewModelScope.launch {
            _approveDriverState.value = Resource.LOADING
            _approveDriverState.emit(useCase.invoke(driverId = driverId))
        }
    }
}