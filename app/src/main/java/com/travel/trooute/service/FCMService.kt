package com.travel.trooute.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.CHANNEL_DESCRIPTION
import com.travel.trooute.core.util.Constants.CHANNEL_ID
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.presentation.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle the received notification here
        val title = message.notification?.title
        val body = message.notification?.body

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        checkNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSound)

        val notificationManager: NotificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notificationBuilder = notification.build()

        // Remove notification if user click on notification
        notificationBuilder.flags = notificationBuilder.flags or Notification.FLAG_AUTO_CANCEL

        notificationManager.notify(1, notificationBuilder)
    }


    private fun checkNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = CHANNEL_DESCRIPTION
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sharedPreferenceManager.saveDeviceId(token)
    }

}