package com.travel.trooute.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.travel.trooute.R
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.databinding.ActivityOnBoardingScreenBinding
import com.travel.trooute.presentation.adapters.OnboardingVPAdapter
import com.travel.trooute.presentation.ui.BaseActivity
import com.travel.trooute.presentation.ui.auth.SignInActivity
import com.travel.trooute.presentation.ui.auth.SignUpActivity
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingScreenActivity : BaseActivity() {

    private val TAG = "OnBoardingScreenActivity"

    private lateinit var binding: ActivityOnBoardingScreenBinding

    private val onboardingVPAdapter: OnboardingVPAdapter by lazy {
        OnboardingVPAdapter(this, this)
    }

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding_screen)

        statusBarColor(R.color.secondary_athens_gray_color)

        binding.apply {
            onBoardingViewPager.adapter = onboardingVPAdapter
            onBoardingViewPager.offscreenPageLimit = 1
            onBoardingDotIndicator.attachTo(onBoardingViewPager)

            onBoardingViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            btnNext1.isVisible = true
                            ltPrevNextButtons.isVisible = false
                            ltSignUpSingInButtons.isVisible = false
                        }

                        1 -> {
                            btnNext1.isVisible = false
                            ltPrevNextButtons.isVisible = true
                            ltSignUpSingInButtons.isVisible = false
                        }

                        else -> {
                            btnNext1.isVisible = false
                            ltPrevNextButtons.isVisible = false
                            ltSignUpSingInButtons.isVisible = true
                        }
                    }
                }
            })

            btnNext1.setOnClickListener {
                onBoardingViewPager.currentItem++
            }

            btnNext2.setOnClickListener {
                onBoardingViewPager.currentItem++
            }

            btnPrevious.setOnClickListener {
                onBoardingViewPager.currentItem--
            }

            btnSignUp.setOnClickListener {
                sharedPreferenceManager.saveOnBoardingState(true)
                startActivity(Intent(this@OnBoardingScreenActivity, SignUpActivity::class.java))
                finish()
            }

            btnSignIn.setOnClickListener {
                sharedPreferenceManager.saveOnBoardingState(true)
                startActivity(Intent(this@OnBoardingScreenActivity, SignInActivity::class.java))
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Code that you need to execute on back press, e.g. finish()
            if (binding.onBoardingViewPager.currentItem == 0) {
                finish()
            } else {
                binding.onBoardingViewPager.currentItem--
            }
        }

    }
}