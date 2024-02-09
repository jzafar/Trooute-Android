package com.example.trooute.presentation.viewmodel.wishlistviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.wishlist.WishListResponse
import com.example.trooute.domain.usecase.wishlist.AddToWishListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToWishListViewModel @Inject constructor(
    private val useCase: AddToWishListUseCase
) : ViewModel() {
    private val _addToWishListState = MutableStateFlow<Resource<User>>(Resource.LOADING)
    val addToWishListState: StateFlow<Resource<User>> get() = _addToWishListState

    fun addToWishList(id:String?) = viewModelScope.launch {
        _addToWishListState.emit(Resource.LOADING)
        _addToWishListState.emit(useCase.invoke(id))
    }
}