package com.travel.trooute.presentation.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.internal.ViewUtils
import com.google.firebase.messaging.FirebaseMessaging
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.auth.request.UpdateDeviceIdRequest
import com.travel.trooute.databinding.ActivityMainBinding
import com.travel.trooute.presentation.adapters.MainBNVMenuAdapter
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showWarningMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.GetMeVM
import com.travel.trooute.presentation.viewmodel.authviewmodel.UpdateDeviceIdVM
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()
    private val getMeViewModel: GetMeVM by viewModels()
    private val updateDeviceIdVM: UpdateDeviceIdVM by viewModels()
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
        askNotificationPermission()
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(receiver, IntentFilter("application_active"))
    }
    override fun onBackPressed() {
        var current = binding.vpMainMenu.currentItem
        if (current > 0) {
            current--
            if (current == 2){
                val currentItem = binding.bnvMainMenu.menu.findItem(R.id.bookingsFragment)
                currentItem.isChecked = true
            } else if (current == 1) {
                val currentItem = binding.bnvMainMenu.menu.findItem(R.id.inboxFragment)
                currentItem.isChecked = true
            } else if (current == 0) {
                val currentItem = binding.bnvMainMenu.menu.findItem(R.id.homeFragment)
                currentItem.isChecked = true
            }

            binding.vpMainMenu.setCurrentItem(current, true)

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

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                Log.i("tag","Receive notification")
                getMeViewModel.getMe()
                bindGetMeApi()
            }
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun bindGetMeApi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                getMeViewModel.getMeState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(TAG, "getMe: Error -> " + it.message.toString())
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            it.data.data?.let { user ->
                                sharedPreferenceManager.saveIsDriverStatus(user.isApprovedDriver)
                                sharedPreferenceManager.saveDriverMode(user.driverMode)
                                sharedPreferenceManager.updateUserInPref(user)
                            }
                            Log.i(TAG, "getMe: success -> " + it.data)
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getToken()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            getToken()
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            getToken()
        } else {
            Toast(this@MainActivity).showWarningMessage(
                this@MainActivity,
                "You'll not get notification if your trip is updated"
            )
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            sharedPreferenceManager.saveDeviceId(token)
            sendTokenToServer()
        })
    }

    private fun sendTokenToServer(){
        val token = sharedPreferenceManager.getDeviceId()
        val request = token?.let { UpdateDeviceIdRequest("android", it) }
        if (request != null) {
            updateDeviceIdVM.updateDeviceId(request)
        }
    }

}