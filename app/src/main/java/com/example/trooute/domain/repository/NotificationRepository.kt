package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.notification.NotificationResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun subscribeTopic(topic: String): Flow<Resource<Boolean>>

    fun unsubscribeTopic(topic: String): Flow<Resource<Boolean>>

    fun getToken(): Flow<Resource<String>>

    suspend fun sendMessageNotification(pushNotification: NotificationRequest): Resource<NotificationResponse>
}