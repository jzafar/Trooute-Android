package com.travel.trooute.presentation.ui.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.travel.trooute.R
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.presentation.ui.BaseActivity
import com.travel.trooute.presentation.ui.auth.SignInActivity
import com.travel.trooute.presentation.ui.main.MainActivity
import com.travel.trooute.presentation.utils.WindowsManager.fullScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity() {

    private val TAG = "SplashScreen"

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        fullScreen()

        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPreferenceManager.getOnBoardingState()) {
                Log.e(TAG, "onCreate: iddd -> " + sharedPreferenceManager.getAuthIdFromPref())
                if (sharedPreferenceManager.getAuthIdFromPref() == "AuthID") {
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                startActivity(Intent(this, OnBoardingScreenActivity::class.java))
            }
            finish()
        }, 3000)
    }
}