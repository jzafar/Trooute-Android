package com.example.trooute.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trooute.presentation.ui.booking.BookingsFragment
import com.example.trooute.presentation.ui.chat.InboxFragment
import com.example.trooute.presentation.ui.setting.SettingsFragment
import com.example.trooute.presentation.ui.trip.HomeFragment

class MainBNVMenuAdapter(
    fragmentActivity: FragmentActivity?
) : FragmentStateAdapter(fragmentActivity!!) {

    private var homeFragment: HomeFragment? = null
    private var inboxFragment: InboxFragment? = null
    private var bookingsFragment: BookingsFragment? = null
//    private var notificationsFragment: NotificationsFragment? = null
    private var settingsFragment: SettingsFragment? = null

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                homeFragment ?: HomeFragment().also { homeFragment = it }
            }

            1 -> {
                inboxFragment ?: InboxFragment().also { inboxFragment = it }
            }

            2 -> {
                bookingsFragment ?: BookingsFragment().also { bookingsFragment = it }
            }

//            3 -> {
//                notificationsFragment ?: NotificationsFragment().also { notificationsFragment = it }
//            }

            3 -> {
                settingsFragment ?: SettingsFragment().also { settingsFragment = it }
            }

            else -> {
                homeFragment ?: HomeFragment().also { homeFragment = it }
            }
        }
    }

    override fun getItemCount(): Int = 4
}