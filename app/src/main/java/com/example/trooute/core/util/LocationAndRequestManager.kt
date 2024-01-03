package com.example.trooute.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/*************************************************************************************************************
 * Help links
 * StackOverFlow -> https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
 * PermissionOfficialDoc -> https://developer.android.com/training/location/permissions
 * OfficialDoc -> https://developer.android.com/training/basics/intents/result
 ************************************************************************************************************/

class LocationAndRequestManager constructor(
    private val context: FragmentActivity,
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {
    private val TAG = "LocationManager"

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
    // how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var backgroundLocationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var gpsActivityResultCallback: ActivityResultLauncher<Intent>

    private var gpsDialog: DialogInterface? = null

    companion object {
        private const val LOCATION_PERMISSION_KEY = "RequestLocationPermission"
        private const val BACKGROUND_LOCATION_PERMISSION_KEY = "RequestBackgroundLocationPermission"
        private const val GPS_PERMISSION_KEY = "RequestGPSPermission"
    }

    fun getLocation(callBack: ((Location) -> Unit)? = null) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (::fusedLocationProvider.isInitialized) {
                fusedLocationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            // Handle the last known location here
                            Log.e(
                                TAG,
                                "getLocation: lat -> ${location.latitude}, Long -> ${location.longitude}"
                            )
                            callBack?.invoke(location)
                        } else {
                            // Location is null, handle the case where the last known location is not available
                            Toast.makeText(
                                context,
                                "Last known location not available",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that occurred while retrieving the last known location
                        Toast.makeText(
                            context,
                            "Error getting last known location: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()

        locationPermissionLauncher = registry.register(
            LOCATION_PERMISSION_KEY,
            owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Location permission granted
                // Proceed with location-related tasks
                if (isGpsEnabled()) {
                    getLocation()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        showEnableGpsDialog()
                    }
                }
            } else {
                // Location permission denied
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()

                // Check if we are in a state where the user has denied the permission and
                // selected Don't ask again
                if (!shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    openLocationSetting()
                }
            }
        }

        backgroundLocationPermissionLauncher = registry.register(
            BACKGROUND_LOCATION_PERMISSION_KEY,
            owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Background location permission granted
                // Proceed with background location-related tasks
                if (isGpsEnabled()) {
                    getLocation()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        showEnableGpsDialog()
                    }
                }
            } else {
                // Background location permission denied
                Toast.makeText(context, "Background location permission denied", Toast.LENGTH_LONG)
                    .show()
                openLocationSetting()
            }
        }

        gpsActivityResultCallback = registry.register(
            GPS_PERMISSION_KEY,
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // GPS is enabled, proceed with your desired actions
                getLocation()
            } else {
                // GPS is still not enabled, handle accordingly
            }
        }

        checkLocationPermission()
    }

    private fun openLocationSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getLocation()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(context)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { _, _ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    @SuppressLint("ServiceCast")
    private fun isGpsEnabled(): Boolean {
        // Check if GPS is enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showEnableGpsDialog() {
        // Show dialog to prompt user to enable GPS
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enable GPS")
            .setMessage("GPS is required for this app. Please enable GPS in your device settings.")
            .setPositiveButton("Settings") { dialog, _ ->
                // Open settings screen to enable GPS
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                gpsActivityResultCallback.launch(intent)
                gpsDialog = dialog
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // Handle cancel action
                gpsDialog = dialog
            }
            .setCancelable(false)
            .show()
    }
}