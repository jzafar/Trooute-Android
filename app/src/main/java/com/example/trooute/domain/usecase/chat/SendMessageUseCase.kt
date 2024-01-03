package com.example.trooute.domain.usecase.chat

import com.example.trooute.data.model.chat.Inbox
import com.example.trooute.data.model.chat.Message
import com.example.trooute.data.model.chat.Users
import com.example.trooute.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(
        currentUser: Users?, inbox: Inbox, message: Message
    ) = repository.sendMessage(currentUser, inbox, message)
}