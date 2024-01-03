package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.GooglePlacesManager
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivitySearchForTripsBinding
import com.example.trooute.presentation.adapters.TripsAdapter
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.core.util.Constants.PLACES_DESTINATION_LAT_LNG
import com.example.trooute.core.util.Constants.PLACES_START_LAT_LNG
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetSearchedTripsViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchForTripsActivity : AppCompatActivity(), AdapterItemClickListener {

    private val TAG = "SearchForTrips"

    private lateinit var binding: ActivitySearchForTripsBinding
    private lateinit var tripsAdapter: TripsAdapter
    private lateinit var skeleton: Skeleton
    private lateinit var rvSkeleton: Skeleton
    private lateinit var googlePlacesManager: GooglePlacesManager

    private var placesStartLocationLatLng: LatLng? = null
    private var placesStartLocationAddress: String? = null
    private var placesDestinationLocationLatLng: LatLng? = null
    private var placesDestinationLocationAddress: String? = null
    private var isStartLocationRequired = false

    private val getSearchedTripsViewModel: GetSearchedTripsViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_for_trips)
        tripsAdapter = TripsAdapter(
            sharedPreferenceManager = sharedPreferenceManager, adapterItemClickListener = this
        )

        placesStartLocationLatLng = IntentCompat.getParcelableExtra(
            intent, PLACES_START_LAT_LNG, LatLng::class.java
        )
        placesDestinationLocationLatLng = IntentCompat.getParcelableExtra(
            intent, PLACES_DESTINATION_LAT_LNG, LatLng::class.java
        )

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Search for Trips"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = skeletonLayout
            skeleton.showSkeleton()

            googlePlacesManager = GooglePlacesManager(
                this@SearchForTripsActivity,
                activityResultRegistry
            ) { placesAddress, placesLatLng ->
                Log.e(TAG, "PlacesAddress: $placesAddress \n PlacesLatLng: $placesLatLng")
                binding.includeTripDestinationLayout.apply {
                    if (isStartLocationRequired) {
                        placesStartLocationLatLng = placesLatLng
                        placesStartLocationAddress = placesAddress
                        etStartingLocation.setText(placesAddress)
                    } else {
                        placesDestinationLocationLatLng = placesLatLng
                        placesDestinationLocationAddress = placesAddress
                        etDestinationLocation.setText(placesAddress)
                    }
                }
            }
            lifecycle.addObserver(googlePlacesManager)

            includeTripDestinationLayout.apply {
                etStartingLocation.setOnClickListener {
                    isStartLocationRequired = true
                    googlePlacesManager.launchGooglePlaces()
                }

                etDestinationLocation.setOnClickListener {
                    isStartLocationRequired = false
                    googlePlacesManager.launchGooglePlaces()
                }
            }

            rvTrips.apply {
                setRVVertical()
                adapter = tripsAdapter
                rvSkeleton = this.applySkeleton(R.layout.rv_trips_item)
                rvSkeleton.showSkeleton()
            }

            callGetTripsApi()
        }
    }

    private fun callGetTripsApi() {
        placesStartLocationLatLng?.latitude?.let { startLat ->
            placesStartLocationLatLng?.longitude?.let { startLong ->
                placesDestinationLocationLatLng?.latitude?.let { destLat ->
                    placesDestinationLocationLatLng?.longitude?.let { destLong ->
                        getSearchedTripsViewModel.getSearchedTrips(
                            fromLatitude = startLat,
                            fromLongitude = startLong,
                            whereToLatitude = destLat,
                            whereToLongitude = destLong
                        )
                    }
                }
            }
        }
        bindGetTripsObserver()
    }

    private fun bindGetTripsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getSearchedTripsViewModel.getTripsState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            rvSkeleton.showOriginal()
                            Log.e(TAG, "bindGetTripsObserver: Error -> " + it.message.toString())
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindGetTripsObserver: Success -> " + it.data)
                            if (it.data.data?.isEmpty() == true) {
                                binding.rvTrips.isVisible = false
                                binding.tvNoTripsAvailable.isVisible = true
                            } else {
                                binding.rvTrips.isVisible = true
                                binding.tvNoTripsAvailable.isVisible = false
                                tripsAdapter.submitList(it.data.data)
                            }

                            skeleton.showOriginal()
                            rvSkeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is TripsData) {
            startActivity(Intent(this, TripDetailActivity::class.java).apply {
                putExtra(TRIP_ID, data._id)
            })
        }
    }
}