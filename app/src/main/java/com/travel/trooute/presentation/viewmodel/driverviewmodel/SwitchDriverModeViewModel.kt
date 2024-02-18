package com.travel.trooute.presentation.viewmodel.driverviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.usecase.driver.SwitchDriverModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwitchDriverModeViewModel @Inject constructor(
    private val useCase: SwitchDriverModeUseCase
) : ViewModel() {
    private val _switchDriverState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val switchDriverState: StateFlow<Resource<BaseResponse>> get() = _switchDriverState

    fun switchDriver() {
        viewModelScope.launch {
            _switchDriverState.value = Resource.LOADING
            _switchDriverState.emit(useCase.invoke())
        }
    }
}