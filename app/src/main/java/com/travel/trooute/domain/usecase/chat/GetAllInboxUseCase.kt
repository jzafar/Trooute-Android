package com.travel.trooute.domain.usecase.chat

import com.travel.trooute.domain.repository.ChatRepository
import javax.inject.Inject

class GetAllInboxUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(userId: String) = repository.getAllInbox(userId)
}