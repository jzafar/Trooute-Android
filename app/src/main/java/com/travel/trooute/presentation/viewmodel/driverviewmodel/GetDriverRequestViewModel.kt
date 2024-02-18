package com.travel.trooute.presentation.viewmodel.driverviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.travel.trooute.domain.usecase.driver.GetDriverRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDriverRequestViewModel @Inject constructor(
    private val useCase: GetDriverRequestUseCase
) : ViewModel() {
    private val _getDriverRequestState = MutableStateFlow<Resource<GetDriversRequestsResponse>>(Resource.LOADING)
    val getDriverRequestState: StateFlow<Resource<GetDriversRequestsResponse>> get() = _getDriverRequestState

    fun getDriverRequest() {
        viewModelScope.launch {
            _getDriverRequestState.value = Resource.LOADING
            _getDriverRequestState.emit(useCase.invoke())
        }
    }
}