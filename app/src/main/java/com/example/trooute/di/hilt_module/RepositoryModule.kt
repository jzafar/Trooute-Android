package com.example.trooute.di.hilt_module

import com.example.trooute.data.datasource.notification.NotificationAPI
import com.example.trooute.data.repository.AuthRepositoryImpl
import com.example.trooute.data.repository.BookingRepositoryImpl
import com.example.trooute.data.repository.ChatRepositoryImpl
import com.example.trooute.data.repository.DriverRepositoryImpl
import com.example.trooute.data.repository.NotificationRepositoryImpl
import com.example.trooute.data.repository.ReviewRepositoryImpl
import com.example.trooute.data.repository.TripsRepositoryImpl
import com.example.trooute.data.repository.WishListRepositoryImpl
import com.example.trooute.domain.repository.AuthRepository
import com.example.trooute.domain.repository.BookingRepository
import com.example.trooute.domain.repository.ChatRepository
import com.example.trooute.domain.repository.DriverRepository
import com.example.trooute.domain.repository.NotificationRepository
import com.example.trooute.domain.repository.ReviewRepository
import com.example.trooute.domain.repository.TripsRepository
import com.example.trooute.domain.repository.WishListRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//class RepositoryModule {
//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        authAPI: AuthAPI,
//        coroutineDispatcher: CoroutineDispatcher
//    ): AuthRepository {
//        return AuthRepositoryImpl(authAPI, coroutineDispatcher)
//    }
//
//    @Provides
//    @Singleton
//    fun provideTripsRepository(
//        tripsAPI: TripsAPI,
//        coroutineDispatcher: CoroutineDispatcher
//    ): TripsRepository {
//        return TripsRepositoryImpl(tripsAPI, coroutineDispatcher)
//    }
//
//    @Provides
//    @Singleton
//    fun provideBookingRepository(
//        bookingsAPI: BookingsAPI,
//        coroutineDispatcher: CoroutineDispatcher
//    ): BookingRepository {
//        return BookingRepositoryImpl(bookingsAPI = bookingsAPI, ioDispatcher = coroutineDispatcher)
//    }
//
//    @Provides
//    @Singleton
//    fun provideDriverRepository(
//        driverAPI: DriverAPI,
//        coroutineDispatcher: CoroutineDispatcher
//    ): DriverRepository {
//        return DriverRepositoryImpl(driverAPI = driverAPI, ioDispatcher = coroutineDispatcher)
//    }
//
//    @Provides
//    @Singleton
//    fun provideReviewRepository(
//        reviewAPI: ReviewAPI,
//        coroutineDispatcher: CoroutineDispatcher
//    ): ReviewRepository {
//        return ReviewRepositoryImpl(reviewAPI = reviewAPI, ioDispatcher = coroutineDispatcher)
//    }
//}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun provideTripsRepository(
        tripsRepositoryImpl: TripsRepositoryImpl
    ): TripsRepository

    @Binds
    @Singleton
    abstract fun provideBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    @Singleton
    abstract fun provideDriverRepository(
        driverRepositoryImpl: DriverRepositoryImpl
    ): DriverRepository

    @Binds
    @Singleton
    abstract fun provideReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    @Singleton
    abstract fun provideChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun provideWishListRepository(
        wishListRepositoryImpl: WishListRepositoryImpl
    ): WishListRepository
}