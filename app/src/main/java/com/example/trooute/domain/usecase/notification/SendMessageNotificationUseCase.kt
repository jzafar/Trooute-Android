package com.example.trooute.domain.usecase.notification

import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.domain.repository.NotificationRepository
import javax.inject.Inject

class SendMessageNotificationUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        pushNotification: NotificationRequest
    ) = repository.sendMessageNotification(
        pushNotification = pushNotification
    )
}