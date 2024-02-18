package com.travel.trooute.di.hilt_module

import com.travel.trooute.domain.usecase.chat.ChatUseCases
import com.travel.trooute.domain.usecase.chat.GetAllInboxUseCase
import com.travel.trooute.domain.usecase.chat.GetAllMessagesUseCase
import com.travel.trooute.domain.usecase.chat.SendMessageUseCase
import com.travel.trooute.domain.usecase.chat.UpdateSeenUseCase
import com.travel.trooute.domain.usecase.notification.NotificationUseCases
import com.travel.trooute.domain.usecase.notification.SendMessageNotificationUseCase
import com.travel.trooute.domain.usecase.notification.SubscribeTopicUseCase
import com.travel.trooute.domain.usecase.notification.UnsubscribeTopicUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCasesModule {
    @Singleton
    @Provides
    fun provideChatUseCases(
        getAllInboxUseCase: GetAllInboxUseCase,
        getAllMessagesUseCase: GetAllMessagesUseCase,
        updateSeenUseCase: UpdateSeenUseCase,
        sendMessageUseCase: SendMessageUseCase
    ): ChatUseCases {
        return ChatUseCases(
            getAllInboxUseCase,
            getAllMessagesUseCase,
            updateSeenUseCase,
            sendMessageUseCase
        )
    }

    @Singleton
    @Provides
    fun provideNotificationUseCases(
        subscribeTopicUseCase: SubscribeTopicUseCase,
        unsubscribeTopicUseCase: UnsubscribeTopicUseCase,
        sendMessageNotificationUseCase: SendMessageNotificationUseCase
    ): NotificationUseCases {
        return NotificationUseCases(
            subscribeTopicUseCase,
            unsubscribeTopicUseCase,
            sendMessageNotificationUseCase
        )
    }
}