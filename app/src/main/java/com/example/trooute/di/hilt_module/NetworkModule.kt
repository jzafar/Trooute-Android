package com.example.trooute.di.hilt_module

import com.example.trooute.data.datasource.network.AuthAPI
import com.example.trooute.data.datasource.network.BookingsAPI
import com.example.trooute.data.datasource.network.DriverAPI
import com.example.trooute.data.datasource.network.ReviewAPI
import com.example.trooute.data.datasource.network.TripsAPI
import com.example.trooute.data.datasource.network.WishListAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideAuthAPI(@NetworkRetrofit retrofit: Retrofit): AuthAPI {
        return retrofit.create(AuthAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideTripAPI(@NetworkRetrofit retrofit: Retrofit): TripsAPI {
        return retrofit.create(TripsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingAPI(@NetworkRetrofit retrofit: Retrofit): BookingsAPI {
        return retrofit.create(BookingsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideDriverAPI(@NetworkRetrofit retrofit: Retrofit): DriverAPI {
        return retrofit.create(DriverAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewAPI(@NetworkRetrofit retrofit: Retrofit): ReviewAPI {
        return retrofit.create(ReviewAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWishListAPI(@NetworkRetrofit retrofit: Retrofit): WishListAPI {
        return retrofit.create(WishListAPI::class.java)
    }
}