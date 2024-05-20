package com.travel.trooute.presentation.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.travel.trooute.R
import com.travel.trooute.databinding.ActivitySuccessBinding
import com.travel.trooute.presentation.ui.BaseActivity
import com.travel.trooute.presentation.utils.WindowsManager.fullScreen

class SuccessActivity : BaseActivity() {

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_success)
        fullScreen()

        binding.apply {
            btnBackToHome.setOnClickListener {
                // Move to MainActivity without refreshing or recreating MainActivity
                val intent = Intent(
                    this@SuccessActivity,
                    MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
    }
}