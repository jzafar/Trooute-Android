package com.travel.trooute.presentation.viewmodel.wishlistviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.wishlist.WishListResponse
import com.travel.trooute.domain.usecase.wishlist.GetMyWishListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetMyWishListViewModel @Inject constructor(
    private val useCase: GetMyWishListUseCase
) : ViewModel() {
    private val _getWishListState = MutableStateFlow<Resource<WishListResponse>>(Resource.LOADING)
    val getMyWishListState: StateFlow<Resource<WishListResponse>> get() = _getWishListState

    fun getMyWishList() = viewModelScope.launch {
        _getWishListState.emit(Resource.LOADING)
        _getWishListState.emit(useCase.invoke())
    }
}