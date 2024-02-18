package com.travel.trooute.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.usecase.auth.UpdateMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UpdateMyProfileVM @Inject constructor(private val useCase: UpdateMyProfileUseCase) : ViewModel() {
    private val _updateMyProfileState = MutableStateFlow<Resource<AuthResponse>>(Resource.LOADING)
    val updateMyProfileState: StateFlow<Resource<AuthResponse>> get() = _updateMyProfileState

    fun updateMyProfile(request: MultipartBody){
        viewModelScope.launch {
            _updateMyProfileState.value = Resource.LOADING
            _updateMyProfileState.value = useCase.invoke(request)
        }
    }
}