package com.travel.trooute.presentation.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

public open class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}