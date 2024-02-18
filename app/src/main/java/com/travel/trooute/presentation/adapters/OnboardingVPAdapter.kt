package com.travel.trooute.presentation.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.travel.trooute.R
import com.travel.trooute.presentation.ui.onboarding.OnBoardingFragment

class OnboardingVPAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.onboarding_title_1),
                context.resources.getString(R.string.onboarding_desc_1)
            )

            1 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.onboarding_title_2),
                context.resources.getString(R.string.onboarding_desc_2)
            )

            2 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.onboarding_title_3),
                context.resources.getString(R.string.onboarding_desc_3)
            )

            else -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.onboarding_title_4),
                context.resources.getString(R.string.onboarding_desc_4)
            )
        }
    }
}