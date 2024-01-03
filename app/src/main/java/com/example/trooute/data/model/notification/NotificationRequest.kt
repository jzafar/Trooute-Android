package com.example.trooute.data.model.notification

data class NotificationRequest(
    val `data`: Data? = null,
    val notification: Notification? = null,
    val to: String? = null ?: ""
) {
    data class Data(
        val dl: String? = null ?: "",
        val url: String? = null ?: ""
    )

    data class Notification(
        val title: String? = null ?: "",
        val body: String? = null ?: "",
        val mutable_content: Boolean = false,
        val sound: String? = null ?: ""
    )
}