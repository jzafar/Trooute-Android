package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.interceptor.SessionUtils
import com.example.trooute.core.util.Constants.INTENT_IS_TRIP_WISH_LISTED
import com.example.trooute.core.util.Constants.PLACES_DESTINATION_LAT_LNG
import com.example.trooute.core.util.Constants.PLACES_START_LAT_LNG
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Constants.WISH_LIST_CHECKER_CODE
import com.example.trooute.core.util.GooglePlacesManager
import com.example.trooute.core.util.LocationAndRequestManager
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.FragmentHomeBinding
import com.example.trooute.presentation.adapters.TripsAdapter
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.interfaces.WishListEventListener
import com.example.trooute.presentation.ui.auth.SignInActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetTripsViewModel
import com.example.trooute.presentation.viewmodel.wishlistviewmodel.AddToWishListViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), AdapterItemClickListener, WishListEventListener {

    private val TAG = "HomeFragment"
    private var placesStartLocationLatLng: LatLng? = null
    private var placesStartLocationAddress: String? = null
    private var placesDestinationLocationLatLng: LatLng? = null
    private var placesDestinationLocationAddress: String? = null
    private var isStartLocationRequired = false
    private var tripList: List<TripsData> = listOf()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var tripsAdapter: TripsAdapter
    private lateinit var skeleton: Skeleton
    private lateinit var rvSkeleton: Skeleton
    private lateinit var locationManager: LocationAndRequestManager
    private lateinit var googlePlacesManager: GooglePlacesManager

    private val getTripsViewModel: GetTripsViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()
    private val addToWishListViewModel: AddToWishListViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        tripsAdapter = TripsAdapter(sharedPreferenceManager, this, this)
        locationManager = LocationAndRequestManager(
            requireActivity(),
            requireActivity().activityResultRegistry
        )
        lifecycle.addObserver(locationManager)

        googlePlacesManager = GooglePlacesManager(
            requireActivity(),
            requireActivity().activityResultRegistry
        ) { placesAddress, placesLatLng ->
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

        binding.apply {
            skeleton = skeletonLayout
            skeleton.showSkeleton()

            includeTripDestinationLayout.apply {
                etStartingLocation.setOnClickListener {
                    isStartLocationRequired = true
                    googlePlacesManager.launchGooglePlaces()
                }
                etDestinationLocation.setOnClickListener {
                    isStartLocationRequired = false
                    googlePlacesManager.launchGooglePlaces()
                }
                btnSeekOutTrips.setOnClickListener {
                    if (
                        requireActivity().isFieldValid(
                            etStartingLocation,
                            "Start location"
                        )
                        && requireActivity().isFieldValid(
                            etDestinationLocation,
                            "Destination location"
                        )
                    ) {
                        startActivity(
                            Intent(requireContext(), SearchForTripsActivity::class.java).apply {
                                putExtra(PLACES_START_LAT_LNG, placesStartLocationLatLng)
                                putExtra(
                                    PLACES_DESTINATION_LAT_LNG,
                                    placesDestinationLocationLatLng
                                )
                            }
                        )
                    }
                }
            }

            rvTrips.apply {
                setRVVertical()
                adapter = tripsAdapter
                rvSkeleton = this.applySkeleton(R.layout.rv_trips_item)
                rvSkeleton.showSkeleton()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (sharedPreferenceManager.driverMode()) {
                includeTripDestinationLayout.cardTripRoute.isVisible = false
                tvTripsTitle.text = getString(R.string.ongoing_trips)
            } else {
                includeTripDestinationLayout.cardTripRoute.isVisible = true
                tvTripsTitle.text = getString(R.string.trips_around_you)
            }

            sharedPreferenceManager.getAuthModelFromPref().let { user ->
                loadProfileImage(imgAuthProfile, user?.photo)
                tvAuthName.text = checkStringValue(
                    requireContext(),
                    user?.name
                )
            }

            locationManager.getLocation { loc ->
                callGetTripsApi(loc)
            }
        }
    }

    private fun callGetTripsApi(location: Location) {
        location.let {
            Log.e(TAG, "callGetTripsApi: lat -> ${it.latitude}, lng -> ${it.longitude}")
            getTripsViewModel.getTrips(
                fromLatitude = it.latitude, fromLongitude = it.longitude
            )
            bindGetTripsObserver()
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun bindGetTripsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                getTripsViewModel.getTripsState.collect {
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

                            // Check if token is expired then unsubscribe the topic
                            if (
                                it.data.message == SessionUtils.sessionMsg1(requireContext())
                                || it.data.message == SessionUtils.sessionMsg2(requireContext())
                                || it.data.message == SessionUtils.sessionMsg3(requireContext())
                            ) {
                                pushNotificationViewModel.unsubscribeTopic("${TROOUTE_TOPIC}${sharedPreferenceManager.getAuthIdFromPref()}")
                                bindUnsubscribeTopicObserver()
                            }


                            if (it.data.data?.isEmpty() == true) {
                                binding.rvTrips.isVisible = false
                                binding.tvNoTripsAvailable.isVisible = true
                            } else {
                                binding.rvTrips.isVisible = true
                                binding.tvNoTripsAvailable.isVisible = false
                                it.data.data?.let {data ->
                                    tripList = data.reversed()
                                    tripsAdapter.submitList(tripList)
                                }
                            }

                            skeleton.showOriginal()
                            rvSkeleton.showOriginal()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun bindUnsubscribeTopicObserver() {
        pushNotificationViewModel.topicState.onEach {
            when (it) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindUnsubscribeTopicObserver: Error -> ${it.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindUnsubscribeTopicObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindUnsubscribeTopicObserver: Success -> ${it.data}")

                    sharedPreferenceManager.saveAuthIdInPref(null)
                    sharedPreferenceManager.saveAuthTokenInPref(null)
                    sharedPreferenceManager.saveAuthModelInPref(null)
                    sharedPreferenceManager.saveIsDriverStatus(null)
                    startActivity(Intent(requireContext(), SignInActivity::class.java))
                    activity?.finish()
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is TripsData) {
            val intent = Intent(requireContext(), TripDetailActivity::class.java)
            intent.putExtra(TRIP_ID, data._id)
            // Pass the current favorite status
            intent.putExtra(INTENT_IS_TRIP_WISH_LISTED, data.isAddedInWishList)
            wishListCheckerLauncher.launch(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val wishListCheckerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == WISH_LIST_CHECKER_CODE) {
                val data: Intent? = result.data
                val companyId = data?.getStringExtra(TRIP_ID)
                val isFavorited = data?.getBooleanExtra(INTENT_IS_TRIP_WISH_LISTED, false) ?: false

                // Update the favorite status of the selected company in your data list or ViewModel
                // Here, assuming you have a list called 'companyList' and a unique identifier 'id' for companies
                tripList.let { trip ->
                    trip.find { it._id == companyId }?.isAddedInWishList = isFavorited
                }

                tripsAdapter.submitList(tripList)
            }
        }

    override fun onWishListEventClick(position: Int, data: Any) {
        if (data is TripsData) {
            addToWishListViewModel.addToWishList(data._id)
            binAddToWishListObserver()
        }
    }

    private fun binAddToWishListObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                addToWishListViewModel.addToWishListState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(
                                TAG,
                                "binAddToWishListObserver: error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "binAddToWishListObserver: success -> " + it.data)
                        }
                    }
                }
            }
        }
    }

}