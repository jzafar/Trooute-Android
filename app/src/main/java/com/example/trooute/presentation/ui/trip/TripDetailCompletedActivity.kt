package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivityTripDetailCompletedBinding
import com.example.trooute.presentation.adapters.TripDetailCompletedAdapter
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.core.util.Constants.WEIGHT_SIGN
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.reviewviewmodel.CreateReviewViewModel
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetTripDetailsViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TripDetailCompletedActivity : AppCompatActivity() {

    private val TAG = "TripDetailCompleted"

    private lateinit var binding: ActivityTripDetailCompletedBinding
    private lateinit var tripID: String
    private lateinit var skeleton: Skeleton
    private lateinit var tripDetailCompletedAdapter: TripDetailCompletedAdapter

    private val getTripDetailsViewModel: GetTripDetailsViewModel by viewModels()
    private val createReviewViewModel: CreateReviewViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail_completed)
        tripID = intent.getStringExtra(TRIP_ID).toString()
        tripDetailCompletedAdapter = TripDetailCompletedAdapter(::submitReviewClicked)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getSubString(tripID)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = ltMainContent.createSkeleton()
            skeleton.showSkeleton()

            rvDriverSidePassengers.apply {
                setRVVertical()
                adapter = tripDetailCompletedAdapter
            }

            includeDriverDetails.apply {
                driverReviewSection.isVisible = true
                includeUserDetailDivider.root.isVisible = true
            }

            getTripDetails()
        }
    }

    private fun getTripDetails() {
        Log.e(TAG, "getTripDetails: tripID -> $tripID")
        getTripDetailsViewModel.getTrips(tripsID = tripID)
        bindGetTripDetailsObserver()
    }

    private fun submitReviewClicked(
        position: Int,
        targetId: String,
        targetType: String,
        comment: String,
        rating: Float,
        trip: String
    ) {
        createReviewViewModel.createReview(
            CreateReviewRequest(
                comment = comment,
                rating = rating,
                targetId = targetId,
                targetType = targetType,
                trip = trip
            )
        )

        bindCreateReviewObserver()
    }

    private fun bindCreateReviewObserver() {
        lifecycleScope.launch {
            createReviewViewModel.createReviewState.collect {
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindCreateReviewObserver: error -> " + it.message.toString())
                    }

                    Resource.LOADING -> {

                    }

                    is Resource.SUCCESS -> {
                        Log.e(
                            TAG,
                            "bindCreateReviewObserver: success -> " + it.data.message.toString()
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindGetTripDetailsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getTripDetailsViewModel.getTripDetailsState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            it.data.data?.let { tripsData ->
                                setupViews(tripsData)
                                skeleton.showOriginal()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews(tripsData: TripsData) {
        Log.e(TAG, "setupViews: tripsData -> $tripsData")
        binding.apply {
            // Booking detail
            includeCompletedBookingDetailItemLayout.apply {
                tvStatus.text = ContextCompat.getString(
                    this@TripDetailCompletedActivity, R.string.completed
                )

                var tripId = tripID.uppercase()
                if (tripsData.trip != null) {
                        tripId = tripsData.trip?._id?.uppercase() ?: tripID.uppercase()
                }

                tvBookingId.text = "Trip # ${
                    checkStringValue(
                        this@TripDetailCompletedActivity,
                        tripId
                    )
                }"

                formatDateTime(
                    this@TripDetailCompletedActivity,
                    tvDepartureDate,
                    tripsData?.departureDate
                )

                ltNxSeats.isVisible = false
                ltPlatformFee.isVisible = false
                includeDivider.divider.isVisible = false
                tvTotalPrice.text = checkPriceValue(tripsData.totalAmount)
                tvTotalPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            }

            // Passengers detail
            if (tripsData.bookings?.isEmpty() == true || tripsData.bookings == null) {
//                tvNoComTripsPassengersAvailable.isVisible = true
//                rvDriverSidePassengers.isVisible = false
                ltPassengersUserSide.isVisible = false
            } else {
//                tvNoComTripsPassengersAvailable.isVisible = false
//                rvDriverSidePassengers.isVisible = true
                ltPassengersUserSide.isVisible = true
                tripDetailCompletedAdapter.submitList(tripsData.bookings)
            }

            // Driver details
            if (sharedPreferenceManager.driverMode()) {
                driverCarInfoLayout.isVisible = false
            } else {
                includeDriverDetails.apply {
                    tripsData?.driver.let { driver ->
                        loadProfileImage(imgUserProfile, driver?.photo)
                        tvUserName.text = checkStringValue(
                            this@TripDetailCompletedActivity,
                            driver?.name
                        )
                        tvAvgRating.text = checkFloatValue(driver?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${checkLongValue(driver?.reviewsStats?.totalReviews)})"

                        includeCarDetails.apply {
                            driver?.carDetails.let { carDetails ->
                                loadImage(imgVehicleProfile, carDetails?.photo)
                                tvVehicleModel.text = checkStringValue(
                                    this@TripDetailCompletedActivity,
                                    carDetails?.model
                                )
                                tvVehicleYear.text = checkLongValue(carDetails?.year)
                                tvVehicleColor.text = checkStringValue(
                                    this@TripDetailCompletedActivity,
                                    carDetails?.color
                                )
                                tvVehicleAvgRating.text = checkFloatValue(
                                    carDetails?.reviewsStats?.avgRating
                                )
                                tvVehicleTotalReviews.text = "(${
                                    checkLongValue(carDetails?.reviewsStats?.totalReviews)
                                })"
                                tvVehicleRegistrationNumber.text = checkStringValue(
                                    this@TripDetailCompletedActivity,
                                    carDetails?.registrationNumber
                                )
                            }
                        }
                    }

                    ltCallInboxSection.isVisible = false
                }
            }


            // Destination and Schedule Details
            includeDestinationAndScheduleLayout.apply {
                includeTripRouteLayout.apply {
                    tvAddressFrom.text = checkStringValue(
                        this@TripDetailCompletedActivity,
                        tripsData?.from_address
                    )
                    formatDateTime(
                        this@TripDetailCompletedActivity,
                        tvDepartureDate,
                        tripsData?.departureDate
                    )
                    tvAddressWhereto.text = checkStringValue(
                        this@TripDetailCompletedActivity,
                        tripsData?.whereTo_address
                    )
                }

                tvPricePerPerson.text = checkPriceValue(tripsData?.pricePerPerson)
            }

            // Trips details
            includeTripDetailLayout.apply {
                tvTypeValue.text = checkStringValue(
                    this@TripDetailCompletedActivity,
                    tripsData?.luggageRestrictions?.text
                )

                tvWeightValue.text = "${
                    checkLongValue(tripsData?.luggageRestrictions?.weight)
                }$WEIGHT_SIGN"

                if (tripsData?.roundTrip == true) {
                    tvRoundTripValue.text = "Yes"
                } else {
                    tvRoundTripValue.text = "No"
                }

                if (tripsData?.smokingPreference == true) {
                    tvSmokingAllowedValue.text = "Yes"
                } else {
                    tvSmokingAllowedValue.text = "No"
                }

                tvLanguagePreferenceValue.text = checkStringValue(
                    this@TripDetailCompletedActivity,
                    tripsData?.languagePreference
                )

                tvOtherRelevantDetailsValue.text = checkStringValue(
                    this@TripDetailCompletedActivity,
                    tripsData?.note
                )
            }
        }
    }
}