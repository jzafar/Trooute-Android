package com.example.trooute.data.model.chat

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val senderId: String? = null ?: "",
    val message: String? = null ?: "",
    val timestamp: Timestamp? = null
) : Parcelable