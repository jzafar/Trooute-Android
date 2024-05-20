package com.travel.trooute.presentation.ui.general

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.travel.trooute.R
import com.travel.trooute.databinding.ActivityPrivacyPolicyBinding
import com.travel.trooute.presentation.ui.BaseActivity

class PrivacyPolicyActivity : BaseActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = intent.getStringExtra("ToolBarTitle")
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }
        }
    }
}