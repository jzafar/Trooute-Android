package com.travel.trooute.core.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.travel.trooute.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.travel.trooute.core.util.Constants.google_map_api_key

class GooglePlacesManager(
    private val context: FragmentActivity,
    private val registry: ActivityResultRegistry,
    private val callBack: (placesAddress: String?, placesLatLng: LatLng?) -> Unit
) : DefaultLifecycleObserver {

    private val TAG = "GooglePlacesManager"

    private lateinit var apiKey: String
    private lateinit var placesLauncher: ActivityResultLauncher<Intent>
    private lateinit var mOwner: LifecycleOwner

    companion object {
        private const val PLACES_LAUNCHER_KEY = "Google Places Launcher"
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        if (!::mOwner.isInitialized) {
            mOwner = owner
        }
        // Get api key
        apiKey = google_map_api_key

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }

        // Create the Autocomplete contract
        placesLauncher = registry.register(
            PLACES_LAUNCHER_KEY,
            mOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        callBack.invoke(place.address, place.latLng)
                    }
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val data = result.data
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }

                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    fun launchGooglePlaces(){

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Start the autocomplete intent.
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)

        if (::placesLauncher.isInitialized)
            placesLauncher.launch(intent)
    }
}