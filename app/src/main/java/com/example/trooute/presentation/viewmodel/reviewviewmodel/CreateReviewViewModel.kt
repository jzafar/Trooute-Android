package com.example.trooute.presentation.viewmodel.reviewviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.domain.usecase.review.CreateReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReviewViewModel  @Inject constructor(
    private val useCase: CreateReviewUseCase
) : ViewModel() {
    private val _createReviewState = MutableStateFlow<Resource<BaseResponse>>(Resource.LOADING)
    val createReviewState: StateFlow<Resource<BaseResponse>> get() = _createReviewState

    fun createReview(request: CreateReviewRequest) {
        viewModelScope.launch {
            _createReviewState.emit(Resource.LOADING)
            _createReviewState.emit(useCase.invoke(request = request))
        }
    }
}