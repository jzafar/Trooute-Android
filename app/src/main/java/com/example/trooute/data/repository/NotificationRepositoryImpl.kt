package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.datasource.notification.NotificationAPI
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.notification.NotificationResponse
import com.example.trooute.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepositoryImpl(
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationAPI: NotificationAPI
) : NotificationRepository {
    override fun subscribeTopic(topic: String): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.LOADING)

            firebaseMessaging.subscribeToTopic(topic).addOnSuccessListener {
                trySend(Resource.SUCCESS(true))
            }.addOnFailureListener {
                trySend(Resource.ERROR("Failed to Subscribe due to ${it.localizedMessage}"))
            }

            awaitClose {
                channel.close()
            }
        } catch (exception: Exception) {
            trySend(Resource.ERROR("Failed to Subscribe due to ${exception.localizedMessage}"))
        }
    }

    override fun unsubscribeTopic(topic: String): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.LOADING)

            firebaseMessaging.unsubscribeFromTopic(topic).addOnSuccessListener {
                trySend(Resource.SUCCESS(true))
            }.addOnFailureListener {
                trySend(Resource.ERROR("Failed to Unsubscribe due to ${it.localizedMessage}"))
            }

            awaitClose {
                channel.close()
            }
        } catch (exception: Exception) {
            trySend(Resource.ERROR("Failed to Unsubscribe due to ${exception.localizedMessage}"))
        }
    }

    override fun getToken(): Flow<Resource<String>> = callbackFlow {
        try {
            trySend(Resource.LOADING)

            firebaseMessaging.token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    trySend(Resource.ERROR("Fetching FCM registration token failed due to ${task.exception}"))
                    return@addOnCompleteListener
                }

                trySend(Resource.SUCCESS(task.result))
            }

            awaitClose {
                channel.close()
            }
        } catch (exception: Exception) {
            trySend(Resource.ERROR("Fetching FCM registration token failed due to ${exception.localizedMessage}"))
        }
    }

    override suspend fun sendMessageNotification(pushNotification: NotificationRequest): Resource<NotificationResponse> {
        return safeApiCall {
            notificationAPI.postNotification(pushNotification)
        }
    }
}