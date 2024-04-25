package com.travel.trooute.presentation.utils

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.travel.trooute.core.util.BroadCastType
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.BROADCAST_INTENT
import com.travel.trooute.core.util.Constants.BROADCAST_TYPE


class AppLifecycleTracker : Application.ActivityLifecycleCallbacks  {

    private var numStarted = 0
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        if (numStarted == 0) {
            Log.i("LifeCycle","Application become active")
            val broadcastIntent = Intent(BROADCAST_INTENT)
            broadcastIntent.putExtra(BROADCAST_TYPE, BroadCastType.FETCH_ME.toString())
            LocalBroadcastManager.getInstance(p0).sendBroadcast(broadcastIntent)
        }
        numStarted++
    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {
        numStarted--
        if (numStarted == 0) {
            // app went to background
            Log.i("LifeCycle","app went to background")
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}