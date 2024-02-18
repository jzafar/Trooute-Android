package com.travel.trooute.domain.usecase.notification

import com.travel.trooute.data.model.notification.NotificationRequest
import com.travel.trooute.domain.repository.NotificationRepository
import javax.inject.Inject

class SendMessageNotificationUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        pushNotification: NotificationRequest
    ) = repository.sendMessageNotification(
        pushNotification = pushNotification
    )
}