package com.example.trooute.di.hilt_module

import com.example.trooute.data.datasource.notification.NotificationAPI
import com.example.trooute.data.repository.NotificationRepositoryImpl
import com.example.trooute.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Singleton
    @Provides
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseMessagingInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideNotificationAPI(@NotificationRetrofit retrofit: Retrofit): NotificationAPI {
        return retrofit.create(NotificationAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firebaseMessaging: FirebaseMessaging,
        notificationAPI: NotificationAPI
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            firebaseMessaging,
            notificationAPI
        )
    }
}