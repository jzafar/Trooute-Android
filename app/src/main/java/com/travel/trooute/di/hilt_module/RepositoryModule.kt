package com.travel.trooute.di.hilt_module

import com.travel.trooute.data.repository.AuthRepositoryImpl
import com.travel.trooute.data.repository.BookingRepositoryImpl
import com.travel.trooute.data.repository.ChatRepositoryImpl
import com.travel.trooute.data.repository.DriverRepositoryImpl
import com.travel.trooute.data.repository.ReviewRepositoryImpl
import com.travel.trooute.data.repository.TripsRepositoryImpl
import com.travel.trooute.data.repository.WishListRepositoryImpl
import com.travel.trooute.domain.repository.AuthRepository
import com.travel.trooute.domain.repository.BookingRepository
import com.travel.trooute.domain.repository.ChatRepository
import com.travel.trooute.domain.repository.DriverRepository
import com.travel.trooute.domain.repository.ReviewRepository
import com.travel.trooute.domain.repository.TripsRepository
import com.travel.trooute.domain.repository.WishListRepository
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