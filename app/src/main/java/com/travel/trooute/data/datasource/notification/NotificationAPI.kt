package com.travel.trooute.data.datasource.notification

import com.travel.trooute.core.util.Constants.CONTENT_TYPE
import com.travel.trooute.core.util.Constants.SERVER_KEY
import com.travel.trooute.data.model.notification.NotificationRequest
import com.travel.trooute.data.model.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// HELP LINK -> https://stackoverflow.com/questions/69397844/flutter-send-firebase-notification-to-token

interface NotificationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: NotificationRequest
    ): Response<NotificationResponse>
}