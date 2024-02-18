package com.travel.trooute.domain.usecase.notification

import com.travel.trooute.domain.repository.NotificationRepository
import javax.inject.Inject

class UnsubscribeTopicUseCase @Inject constructor(private val repository: NotificationRepository) {
    operator fun invoke(topic: String) = repository.unsubscribeTopic(topic = topic)
}