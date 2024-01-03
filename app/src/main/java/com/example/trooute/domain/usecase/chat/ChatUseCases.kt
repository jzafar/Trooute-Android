package com.example.trooute.domain.usecase.chat

import javax.inject.Inject

data class ChatUseCases @Inject constructor(
    val getAllInboxUseCase: GetAllInboxUseCase,
    val getAllMessagesUseCase: GetAllMessagesUseCase,
    val updateSeenUseCase: UpdateSeenUseCase,
    val sendMessageUseCase: SendMessageUseCase
)
