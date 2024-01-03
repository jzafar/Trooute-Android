package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.chat.Inbox
import com.example.trooute.data.model.chat.Message
import com.example.trooute.data.model.chat.Users
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllInbox(userId: String?): Flow<Resource<List<Inbox>>>

    fun getAllMessages(
        senderId: String?,
        receiverId: String?
    ): Flow<Resource<List<Message>>>

    fun updateSeenStatus(
        currentUID: String?,
        receiverID: String?,
        isSeen: Boolean
    ): Flow<Resource<Boolean>>

    fun sendMessage(
        currentUser: Users?,
        inbox: Inbox,
        message: Message
    ): Flow<Resource<Boolean>>
}