package com.travel.trooute.presentation.viewmodel.driverviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.domain.usecase.driver.UploadDriverDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadDriverDetailsViewModel @Inject constructor(
    private val useCase: UploadDriverDetailsUseCase
) : ViewModel() {
    private val _uploadDriverDetailsState =
        MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val uploadDriverDetailsState: StateFlow<Resource<BaseResponse>> get() = _uploadDriverDetailsState

    fun uploadDriverDetails(request: UploadDriverDetailsRequest) {
        viewModelScope.launch {
            _uploadDriverDetailsState.value = Resource.LOADING
            _uploadDriverDetailsState.emit(useCase.invoke(request = request))
        }
    }
}