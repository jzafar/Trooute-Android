package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.example.trooute.core.util.Constants.SEARCH_TRIPS_DATA
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
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetSearchedTripsViewModel
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetTripsViewModel
import com.example.trooute.presentation.viewmodel.wishlistviewmodel.AddToWishListViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), AdapterItemClickListener, WishListEventListener, DatePickerDialog.OnDateSetListener {

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
    private val getSearchedTripsViewModel: GetSearchedTripsViewModel by viewModels()
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
                chooseDate.setOnClickListener {
                    val calendar: Calendar = Calendar.getInstance()
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = calendar.get(Calendar.MONTH)
                    val year = calendar.get(Calendar.YEAR)
                    val datePickerDialog =
                        DatePickerDialog(requireActivity(), R.style.DatePickerTheme,
                            this@HomeFragment as OnDateSetListener?, year, month,day)
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    datePickerDialog.show()
                }
                removeBtn.setOnClickListener {
                    var current = itemQuanEt.text.toString().toInt()
                    if (current > 1) {
                        current--
                        itemQuanEt.setText(current.toString())
                    }
                }
                addBtn.setOnClickListener {
                    var current = itemQuanEt.text.toString().toInt()
                    if (current < 365) {
                        current++
                        itemQuanEt.setText(current.toString())
                    } else {
                        Toast.makeText(
                            context,
                            "Flexible days can't be greater than 365",
                            Toast.LENGTH_LONG
                        ).show()
                    }

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
                        callSearchUserTripApi()
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
        val dateTime = simpleDateFormat.format(calendar.time)

        binding.includeTripDestinationLayout.chooseDate.setText(dateTime)
    }
    override fun onResume() {
        super.onResume()
        binding.apply {
            if (sharedPreferenceManager.driverMode()) {
                includeTripDestinationLayout.cardTripRoute.isVisible = false
                tvTripsTitle.text = getString(R.string.ongoing_trips)
                tvNoTripsAvailable.text = "You do not have any ongoing trip"
            } else {
                includeTripDestinationLayout.cardTripRoute.isVisible = true
                tvTripsTitle.text = getString(R.string.trips_around_you)
                tvNoTripsAvailable.text = getString(R.string.no_trips_around_you)
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

            this.includeTripDestinationLayout.fromSlider.addOnChangeListener { slider, value, fromUser ->
                slider.setLabelFormatter { value -> //It is just an example
                    String.format(Locale.US, "%.0f km", value)
                }
            }

            this.includeTripDestinationLayout.whereSlider.addOnChangeListener { slider, value, fromUser ->
                slider.setLabelFormatter { value -> //It is just an example
                    String.format(Locale.US, "%.0f km", value)
                }
            }
        }
    }

    private fun callGetTripsApi(location: Location?) {
        var departureDate: String? = null
        if (sharedPreferenceManager.driverMode()) {
            departureDate = LocalDate.now().toString()
        }

        location.let {
            Log.e(TAG, "callGetTripsApi: lat -> ${it?.latitude}, lng -> ${it?.longitude}")
            getTripsViewModel.getTrips(
                fromLatitude = it?.latitude, fromLongitude = it?.longitude, departureDate =  departureDate
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

    private fun callSearchUserTripApi() {
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
        bindSearchUserTripsObserver()
    }

    private fun bindSearchUserTripsObserver() {
        var pushedToNextView = false
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getSearchedTripsViewModel.getTripsState.collect {
                    loader.cancel()
                    when (it) {
                        is Resource.ERROR -> {
                            Toast.makeText(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e(TAG, "bindSearchUserTripsObserver: Error -> " + it.message.toString())
                        }

                        Resource.LOADING -> {
                            loader.show()
                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindSearchUserTripsObserver: Success -> " + it.data)
                            if (it.data.data?.isEmpty() == true) {
                                Toast.makeText(
                                    context,
                                    "No trip found",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
//                                binding.rvTrips.isVisible = true
//                                binding.tvNoTripsAvailable.isVisible = false
//                                tripsAdapter.submitList(it.data.data)

                                if (!pushedToNextView) {
                                    pushedToNextView = true
                                    startActivity(
                                        Intent(requireContext(), SearchForTripsActivity::class.java).apply {
//                                         val arrayData = it.data.data?.toArr
//                                        putExtra(SEARCH_TRIPS_DATA, arrayData)
                                            putParcelableArrayListExtra(SEARCH_TRIPS_DATA, ArrayList(it.data.data))
                                        }
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}