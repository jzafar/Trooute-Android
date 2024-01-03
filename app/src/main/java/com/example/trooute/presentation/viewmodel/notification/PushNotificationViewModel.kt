package com.example.trooute.presentation.viewmodel.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.notification.NotificationResponse
import com.example.trooute.domain.usecase.notification.NotificationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushNotificationViewModel @Inject constructor(
    private val useCase: NotificationUseCases
) : ViewModel() {
    private val _topicState = MutableStateFlow<Resource<Boolean>>(Resource.LOADING)
    val topicState = _topicState.asStateFlow()

    fun subscribeTopic(topic: String) = viewModelScope.launch {
        _topicState.emit(Resource.LOADING)
        useCase.subscribeTopicUseCase.invoke(topic).collectLatest {
            _topicState.emit(it)
        }
    }

    fun unsubscribeTopic(topic: String) = viewModelScope.launch {
        _topicState.emit(Resource.LOADING)
        useCase.unsubscribeTopicUseCase.invoke(topic).collectLatest {
            _topicState.emit(it)
        }
    }

    private val _sendNotificationState =
        MutableStateFlow<Resource<NotificationResponse>>(Resource.LOADING)
    val sendNotificationState = _sendNotificationState.asStateFlow()

    fun sendMessageNotification(pushNotification: NotificationRequest) = viewModelScope.launch {
        _sendNotificationState.emit(Resource.LOADING)
        _sendNotificationState.emit(useCase.sendMessageNotificationUseCase.invoke(pushNotification))
    }
}