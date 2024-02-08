package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivityTripDetailCompletedBinding
import com.example.trooute.presentation.adapters.TripDetailCompletedAdapter
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.core.util.Constants.WEIGHT_SIGN
import com.example.trooute.data.model.common.User
import com.example.trooute.data.model.trip.response.Booking
import com.example.trooute.presentation.ui.review.ReviewsActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
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

    private lateinit var tripDriver: String
    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail_completed)
        tripID = intent.getStringExtra(TRIP_ID).toString()
        tripDetailCompletedAdapter = TripDetailCompletedAdapter(::submitReviewClicked,sharedPreferenceManager,::seeUserReview)

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

    private fun seeUserReview(targetId: String){
        startActivity(
            Intent(this@TripDetailCompletedActivity,
                ReviewsActivity::class.java).apply {
                putExtra(Constants.USER_ID, targetId)
            }
        )
    }
    private fun bindCreateReviewObserver() {
        lifecycleScope.launch {
            createReviewViewModel.createReviewState.collect {
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindCreateReviewObserver: error -> " + it.message.toString())
                        Toast(this@TripDetailCompletedActivity).showErrorMessage(
                            this@TripDetailCompletedActivity,
                            it.message.toString()
                        )
                    }

                    Resource.LOADING -> {

                    }

                    is Resource.SUCCESS -> {
                        Log.i(
                            TAG,
                            "bindCreateReviewObserver: success -> " + it.data.message.toString()
                        )
                        Toast(this@TripDetailCompletedActivity).showSuccessMessage(
                            this@TripDetailCompletedActivity,
                            "Review posted successfully"
                        )
                        getTripDetails()
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
                                if (tripsData.driver?._id != null) {
                                    tripDriver = tripsData.driver._id.toString()
                                } else {
                                    if (tripsData.trip?.driver?._id != null) {
                                        tripDriver = tripsData.trip.driver._id
                                    } else {
//                                        tripID = sharedPreferenceManager.getAuthIdFromPref().toString()
                                    }

                                }
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
                ltNxSeats.isVisible = false
                ltPlatformFee.isVisible = false
                if (sharedPreferenceManager.driverMode()) {
                    formatDateTime(
                        this@TripDetailCompletedActivity,
                        tvDepartureDate,
                        tripsData?.trip?.departureDate
                    )
                    includeDivider.divider.isVisible = false
                    tvTotalPrice.text = checkPriceValue(tripsData?.trip?.totalAmount)
                } else {
                    formatDateTime(
                        this@TripDetailCompletedActivity,
                        tvDepartureDate,
                        tripsData?.departureDate
                    )
                    includeDivider.divider.isVisible = true
                    tvTotalPrice.text = checkPriceValue(tripsData?.totalAmount)
                }
                tvTotalPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            }

            // Passengers detail
            if (tripsData.bookings?.isEmpty() == true || tripsData.bookings == null) {
                ltPassengersUserSide.isVisible = false

            } else {
                val bookingList : MutableList<Booking>  = mutableListOf<Booking>()
                ltPassengersUserSide.isVisible = true
                if (sharedPreferenceManager.driverMode()) {
                    for (booking in tripsData.bookings) {
                        booking.pricePerPerson = tripsData.trip?.pricePerPerson
                        booking.driverId = tripDriver
                        bookingList.add(booking)
                    }
                } else {

                    for (booking in tripsData.bookings) {
                        booking.pricePerPerson = tripsData.pricePerPerson
                        booking.driverId = tripDriver
                        bookingList.add(booking)
                    }
                }
                tripDetailCompletedAdapter.submitList(bookingList)

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
                        var genderStr = checkStringValue(
                            this@TripDetailCompletedActivity, driver?.gender
                        )

                        if (genderStr.equals(getString(R.string.not_provided))){
                            gender.isVisible = false
                        }
                        else {
                            gender.text = genderStr
                        }


                        tvAvgRating.text = checkFloatValue(driver?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${checkLongValue(driver?.reviewsStats?.totalReviews)})"

                        // Expending review portion
                        includeReviewItem.apply {
                            tvDriverReviewsTitle.setOnClickListener {
                                ltReviewsItem.apply {
                                    if (isVisible) {
                                        isVisible = false
                                        tvDriverReviewsTitle.setCompoundDrawablesWithIntrinsicBounds(
                                            null, null, getDrawable(
                                                R.drawable.ic_arrow_down
                                            ), null
                                        )
                                    } else {
                                        isVisible = true
                                        tvDriverReviewsTitle.setCompoundDrawablesWithIntrinsicBounds(
                                            null, null, getDrawable(
                                                R.drawable.ic_arrow_up
                                            ), null
                                        )
                                    }
                                }
                            }

                            // Review given to driver from user
                            val userBooking: Booking? = tripsData.bookings?.filter {
                                it.user?._id ==  sharedPreferenceManager.getAuthIdFromPref()
                            }?.single()
                            userBooking?.reviewsGivenToDriver?.let {
                                ltUserReviews.isVisible = true
                                includeDivider.divider.isVisible = true

                                tvUserName.text = checkStringValue(
                                    tvUserName.context,
                                    userBooking.user?.name
                                )
                                tvComment.text = checkStringValue(
                                    tvComment.context,
                                    userBooking.reviewsGivenToDriver.comment
                                )

                                rbExperienceWithDriver.rating = checkFloatValue(
                                    userBooking.reviewsGivenToDriver.rating
                                ).toFloat()
//                                rbRateTheVehicle.rating = checkFloatValue(
//                                    booking.reviewsGivenToCar?.rating
//                                ).toFloat()
                            }

                            run {
                                if (userBooking != null) {
                                    btnSubmitReview.isVisible = false
                                    ltWriteReviews.isVisible = false
                                    ltDriverReview.isVisible = false
                                } else {
                                    btnSubmitReview.isVisible = true
                                    ltWriteReviews.isVisible = true
                                    ltDriverReview.isVisible = false
                                    var submitReviewRatingValue = rbSubmitExperienceWithDriver.rating

                                    rbSubmitExperienceWithDriver.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                                        submitReviewRatingValue = rating
                                        btnSubmitReview.setOnClickListener {
                                            if (
                                                shareYourThoughts.context.isFieldValid(
                                                    shareYourThoughts,
                                                    "Comment Required"
                                                )
                                            ) {
                                                // Handling on client side
                                                val comment = shareYourThoughts.text.toString()
//                                        commentState(binding, comment, submitReviewRatingValue)
                                                val driverId = tripsData.driver?._id
                                                // Handling on server side
                                                if (driverId != null) {
                                                    submitReviewClicked(0,driverId,"Driver",comment,submitReviewRatingValue,tripsData._id)
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // User to Car review
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

                                ltCardReview.isVisible = true
                                includeCarRatingDivider.root.isVisible = true
                                includeCarReviewItem.ltCarReviewsItem.isVisible = false
                                // User to car rating
                                ltCardReview.setOnClickListener{
                                    includeCarReviewItem.ltCarReviewsItem.apply {
                                        val userBooking: Booking? = tripsData.bookings?.filter {
                                            it.user?._id ==  sharedPreferenceManager.getAuthIdFromPref()
                                        }?.single()
                                        userBooking?.reviewsGivenToCar?.let {
                                            includeCarReviewItem.btnSubmitCarReview.isVisible = false
                                            includeCarReviewItem.rbSubmitExperienceWithDriver.rating =
                                                checkFloatValue(
                                                    it.rating
                                                ).toFloat()
                                        }
                                        if (isVisible) {
                                            isVisible = false
                                            tvCarReviewsTitle.setCompoundDrawablesWithIntrinsicBounds(
                                                null, null, getDrawable(
                                                    R.drawable.ic_arrow_down
                                                ), null
                                            )
                                        } else {
                                            isVisible = true
                                            tvCarReviewsTitle.setCompoundDrawablesWithIntrinsicBounds(
                                                null, null, getDrawable(
                                                    R.drawable.ic_arrow_up
                                                ), null
                                            )
                                        }
                                        includeCarReviewItem.btnSubmitCarReview.setOnClickListener{
                                            var submitReviewRatingValue = checkFloatValue(
                                                includeCarReviewItem.rbSubmitExperienceWithDriver.rating
                                            ).toFloat()
                                            if (submitReviewRatingValue.toDouble() == 0.0) {
                                                Toast(this@TripDetailCompletedActivity).showErrorMessage(
                                                    this@TripDetailCompletedActivity,
                                                    "Please select rating"
                                                )
                                                return@setOnClickListener
                                            }
                                            driver?._id?.let { it1 ->
                                                submitReviewClicked(0,
                                                    it1,"Car","", submitReviewRatingValue,tripsData._id)
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    ltCallInboxSection.isVisible = false
                }

            }

            // Destination and Schedule Details
            includeDestinationAndScheduleLayout.apply {
                includeTripRouteLayout.apply {
                    if (sharedPreferenceManager.driverMode()) {
                        tvAddressFrom.text = checkStringValue(
                            this@TripDetailCompletedActivity,
                            tripsData?.trip?.from_address
                        )
                        formatDateTime(
                            this@TripDetailCompletedActivity,
                            tvDepartureDate,
                            tripsData?.trip?.departureDate
                        )
                        tvAddressWhereto.text = checkStringValue(
                            this@TripDetailCompletedActivity,
                            tripsData?.trip?.whereTo_address
                        )
                        tvPricePerPerson.text = checkPriceValue(tripsData?.trip?.pricePerPerson)
                    } else {
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
                        tvPricePerPerson.text = checkPriceValue(tripsData?.pricePerPerson)
                    }

                }


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