package com.travel.trooute.domain.usecase.chat

import com.travel.trooute.domain.repository.ChatRepository
import javax.inject.Inject

class GetAllMessagesUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(
        senderId: String?,
        receiverId: String?
    ) = repository.getAllMessages(senderId, receiverId)
}