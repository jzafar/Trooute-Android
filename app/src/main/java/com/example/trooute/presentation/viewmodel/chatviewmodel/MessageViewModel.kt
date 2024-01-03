package com.example.trooute.presentation.viewmodel.chatviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.chat.Inbox
import com.example.trooute.data.model.chat.Message
import com.example.trooute.data.model.chat.Users
import com.example.trooute.domain.usecase.chat.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val useCase: ChatUseCases
) : ViewModel() {
    private val _allMessagesState = MutableStateFlow<Resource<List<Message>>>(Resource.LOADING)
    val allMessagesState = _allMessagesState.asStateFlow()

    fun getAllMessages(
        senderId: String?, receiverId: String?
    ) = viewModelScope.launch {
        _allMessagesState.emit(Resource.LOADING)
        useCase.getAllMessagesUseCase.invoke(
            senderId, receiverId
        ).collect {
            _allMessagesState.emit(it)
        }
    }

    private val _messageState = MutableStateFlow<Resource<Boolean>>(Resource.LOADING)
    val messageState = _messageState.asStateFlow()

    fun updateSeenStatus(
        currentUID: String?, receiverID: String?, isSeen: Boolean
    ) = viewModelScope.launch {
        _messageState.emit(Resource.LOADING)
        useCase.updateSeenUseCase.invoke(
            currentUID, receiverID, isSeen
        ).collectLatest {
            _messageState.emit(it)
        }
    }

    fun sendMessage(
        currentUser: Users?, inbox: Inbox, message: Message
    ) = viewModelScope.launch {
        _messageState.emit(Resource.LOADING)
        useCase.sendMessageUseCase.invoke(currentUser, inbox, message).collectLatest {
            _messageState.emit(it)
        }
    }
}