package com.travel.trooute.domain.usecase.chat

import com.travel.trooute.domain.repository.ChatRepository
import javax.inject.Inject

class UpdateSeenUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(
        currentUID: String?, receiverID: String?, isSeen: Boolean
    ) = repository.updateSeenStatus(currentUID, receiverID, isSeen)
}