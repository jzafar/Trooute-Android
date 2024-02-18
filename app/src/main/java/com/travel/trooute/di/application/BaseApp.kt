package com.travel.trooute.di.application

import android.app.Application
import com.travel.trooute.presentation.utils.AppLifecycleTracker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppLifecycleTracker())
    }
}