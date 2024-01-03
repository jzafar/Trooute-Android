package com.example.trooute.domain.usecase.notification

data class NotificationUseCases(
    val subscribeTopicUseCase: SubscribeTopicUseCase,
    val unsubscribeTopicUseCase: UnsubscribeTopicUseCase,
    val sendMessageNotificationUseCase: SendMessageNotificationUseCase
)
