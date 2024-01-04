package com.example.trooute.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.trooute.R
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.databinding.ActivityMainBinding
import com.example.trooute.presentation.adapters.MainBNVMenuAdapter
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupAppBar(false, getString(R.string.home), false)

        Log.e(TAG, "onCreate")

        binding.apply {
            // Setup viewPager2
            vpMainMenu.isUserInputEnabled = false
            vpMainMenu.offscreenPageLimit = 4

            // Setup bottom navigation view adapter
            val mainBNVMenuAdapter = MainBNVMenuAdapter(this@MainActivity)
            mainBNVMenuAdapter.createFragment(0)
            mainBNVMenuAdapter.createFragment(1)
            mainBNVMenuAdapter.createFragment(2)
            mainBNVMenuAdapter.createFragment(3)
//            mainBNVMenuAdapter.createFragment(4)

            // Set adapter to viewPager2
            vpMainMenu.adapter = mainBNVMenuAdapter

            // Set click listener on bottom navigation view
            bnvMainMenu.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.homeFragment -> {
                        vpMainMenu.currentItem = 0
                    }

                    R.id.inboxFragment -> {
                        vpMainMenu.currentItem = 1
                    }

                    R.id.bookingsFragment -> {
                        vpMainMenu.currentItem = 2
                    }

//                    R.id.notificationsFragment -> {
//                        vpMainMenu.currentItem = 3
//                    }

                    R.id.settingsFragment -> {
                        vpMainMenu.currentItem = 3
                    }
                }
                when (vpMainMenu.currentItem) {
                    0 -> {
                        setupAppBar(false, getString(R.string.home), false)
                    }

                    1 -> {
                        setupAppBar(true, getString(R.string.inbox), false)
                    }

                    2 -> {
                        setupAppBar(true, getString(R.string.bookings), true)
                    }

//                    3 -> {
//                        setupAppBar(true, getString(R.string.notifications), false)
//                    }

                    3 -> {
                        setupAppBar(false, getString(R.string.bookings), false)
                    }

                    else -> {
                        setupAppBar(false, getString(R.string.home), false)
                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun setupAppBar(
        isAppBarVisible: Boolean,
        title: String,
        isActionIconVisible: Boolean
    ) {
        binding.includeAppBar.apply {
            this.appBarLayout.isVisible = isAppBarVisible
            this.toolbarTitle.text = title
            this.arrowBackPress.isVisible = false
            this.filter.isVisible = false //isActionIconVisible
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e(TAG, "onNewIntent: called")
        intent?.let {
            if (it.getBooleanExtra("trip_booked", false)) {
                setupAppBar(true, getString(R.string.bookings), true)
                binding.vpMainMenu.currentItem = 2
                val menu: Menu = binding.bnvMainMenu.menu
                // Find the second menu item
                val bookingsFragment: MenuItem = menu.findItem(R.id.bookingsFragment)
                // Set the second menu item as selected
                bookingsFragment.isChecked = true
            }
        }
    }

    private fun bindSendMessageNotificationObserver() {
        pushNotificationViewModel.sendNotificationState.onEach { state ->
            when (state) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Error -> ${state.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Success -> ${state.data}")
                }
            }
        }.launchIn(lifecycleScope)
    }
}