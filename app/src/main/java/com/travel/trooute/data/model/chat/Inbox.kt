package com.travel.trooute.data.model.chat

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inbox(
    val user: Users? = null,
    val users: List<Users>? = null,
    val lastMessage: String? = null ?: "",
    val timestamp: Timestamp? = null,
    val message: Message? = null
) : Parcelable
