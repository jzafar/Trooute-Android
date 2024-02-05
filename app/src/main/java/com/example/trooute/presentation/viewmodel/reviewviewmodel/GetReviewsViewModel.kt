package com.example.trooute.presentation.viewmodel.reviewviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.review.response.GetReviewsResponse
import com.example.trooute.domain.usecase.review.GetReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetReviewsViewModel @Inject constructor(
    private val useCase: GetReviewsUseCase
) : ViewModel() {
    private val _getReviewsState = MutableStateFlow<Resource<GetReviewsResponse>>(
        Resource.LOADING)
    val getReviewsState: StateFlow<Resource<GetReviewsResponse>> get() = _getReviewsState

    fun getReviews(userId: String) {
        viewModelScope.launch {
            _getReviewsState.value = Resource.LOADING
            _getReviewsState.emit(useCase.invoke(userId = userId))
        }
    }
}