package com.travel.trooute.domain.usecase.chat

import com.travel.trooute.data.model.chat.Inbox
import com.travel.trooute.data.model.chat.Message
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(
        currentUser: Users?, inbox: Inbox, message: Message
    ) = repository.sendMessage(currentUser, inbox, message)
}