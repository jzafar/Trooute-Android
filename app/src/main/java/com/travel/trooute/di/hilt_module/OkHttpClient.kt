package com.travel.trooute.di.hilt_module

import android.content.Context
import com.travel.trooute.core.connectivity_monitor.NetworkStatusTracker
import com.travel.trooute.core.interceptor.AuthInterceptor
import com.travel.trooute.core.interceptor.ConnectionInterceptor
import com.travel.trooute.core.interceptor.TokenInterceptor
import com.travel.trooute.core.util.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClient {
    companion object {
        //        private const val READ_TIMEOUT = 30
        private const val READ_TIMEOUT = 500

        //        private const val WRITE_TIMEOUT = 30
        private const val WRITE_TIMEOUT = 500

        //        private const val CONNECTION_TIMEOUT = 10
        private const val CONNECTION_TIMEOUT = 500
        private const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
    }

    @Provides
    @Singleton
    internal fun provideCache(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        context: Context,
        sharedPreferenceManager: SharedPreferenceManager,
    ): AuthInterceptor {
        return AuthInterceptor(context, sharedPreferenceManager)
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(
        sharedPreferenceManager: SharedPreferenceManager
    ): TokenInterceptor = TokenInterceptor(sharedPreferenceManager)

    @Provides
    @Singleton
    fun provideNetworkStatusTracker(context: Context): NetworkStatusTracker {
        return NetworkStatusTracker(context = context)
    }

    @Provides
    @Singleton
    fun provideConnectionInterceptor(
        context: Context,
        networkStatusTracker: NetworkStatusTracker
    ): ConnectionInterceptor {
        return ConnectionInterceptor(context = context, networkStatusTracker = networkStatusTracker)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: TokenInterceptor,
        authInterceptor: AuthInterceptor,
        connectionInterceptor: ConnectionInterceptor,
//        cachingInterceptor: CachingInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.cache(cache)
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(connectionInterceptor)
//            .addInterceptor(cachingInterceptor)
        return okHttpClientBuilder.build()
    }
}