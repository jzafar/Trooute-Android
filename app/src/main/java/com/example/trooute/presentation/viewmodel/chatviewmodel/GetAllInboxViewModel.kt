package com.example.trooute.presentation.viewmodel.chatviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.chat.Inbox
import com.example.trooute.domain.usecase.chat.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAllInboxViewModel @Inject constructor(private val useCase: ChatUseCases) :
    ViewModel() {
    private val _getAllInboxState = MutableStateFlow<Resource<List<Inbox>>>(Resource.LOADING)
    val getAllInboxState = _getAllInboxState.asStateFlow()

    fun getAllInbox(userId: String) = viewModelScope.launch {
        _getAllInboxState.emit(Resource.LOADING)
        useCase.getAllInboxUseCase.invoke(userId).collectLatest {
            _getAllInboxState.emit(it)
        }
    }
}