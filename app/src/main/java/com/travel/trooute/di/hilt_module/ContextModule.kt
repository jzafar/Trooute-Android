package com.travel.trooute.di.hilt_module

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContextModule {
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideAppCompatActivityContext(appCompatActivity: AppCompatActivity): AppCompatActivity = appCompatActivity

    @Provides
    @Singleton
    fun provideFragmentActivityContext(fragmentActivity: FragmentActivity): FragmentActivity = fragmentActivity
}