package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants.BOOKED_CANCELLED_BODY
import com.example.trooute.core.util.Constants.BOOKED_CANCELLED_TITLE
import com.example.trooute.core.util.Constants.CANCELED
import com.example.trooute.core.util.Constants.COMPLETED
import com.example.trooute.core.util.Constants.GET_TRIP_DETAIL
import com.example.trooute.core.util.Constants.INTENT_IS_TRIP_WISH_LISTED
import com.example.trooute.core.util.Constants.IN_PROGRESS
import com.example.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.example.trooute.core.util.Constants.MUTABLE_CONTENT
import com.example.trooute.core.util.Constants.SCHEDULED
import com.example.trooute.core.util.Constants.START_BOOKING_BODY
import com.example.trooute.core.util.Constants.START_BOOKING_TITLE
import com.example.trooute.core.util.Constants.TONE
import com.example.trooute.core.util.Constants.TOPIC
import com.example.trooute.core.util.Constants.TRIP_COMPLETED_BODY
import com.example.trooute.core.util.Constants.TRIP_COMPLETED_TITLE
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Constants.WEIGHT_SIGN
import com.example.trooute.core.util.Constants.WISH_LIST_CHECKER_CODE
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.chat.Users
import com.example.trooute.data.model.common.User
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.trip.response.Booking
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivityTripDetailBinding
import com.example.trooute.presentation.adapters.DriverSidePassengersAdapter
import com.example.trooute.presentation.adapters.PassengersPrimaryAdapter
import com.example.trooute.presentation.ui.booking.BookNowActivity
import com.example.trooute.presentation.ui.chat.MessageActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVHorizontal
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.example.trooute.presentation.viewmodel.tripviewmodel.GetTripDetailsViewModel
import com.example.trooute.presentation.viewmodel.tripviewmodel.UpdateTripStatusViewModel
import com.example.trooute.presentation.viewmodel.wishlistviewmodel.AddToWishListViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TripDetailActivity : AppCompatActivity() {

    private val TAG = "TripDetailActivity"

    private lateinit var binding: ActivityTripDetailBinding
    private lateinit var tripID: String
    private lateinit var skeleton: Skeleton
    private lateinit var passengersAdapter: PassengersPrimaryAdapter
    private lateinit var driverSidePassengersAdapter: DriverSidePassengersAdapter

    private var isScreenNeedToFinish = false

    private var authModelInfo: com.example.trooute.data.model.auth.response.User? = null

    private val getTripDetailsViewModel: GetTripDetailsViewModel by viewModels()
    private val updateTripStatusViewModel: UpdateTripStatusViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()
    private val addToWishListViewModel: AddToWishListViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail)
        tripID = intent.getStringExtra(TRIP_ID).toString()
        passengersAdapter = PassengersPrimaryAdapter()
        driverSidePassengersAdapter =
            DriverSidePassengersAdapter(sharedPreferenceManager, ::startMessaging, ::startCall)

        authModelInfo = sharedPreferenceManager.getAuthModelFromPref()

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Trip Detail"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = ltMainContent.createSkeleton()
            skeleton.showSkeleton()

            if (sharedPreferenceManager.driverMode()) {
                ltPassengersUserSide.isVisible = false
                ltDriverSidePassengers.isVisible = true
                tvMyDetailTitle.isVisible = true

                rvDriverSidePassengers.apply {
                    this.setRVHorizontal()
                    adapter = driverSidePassengersAdapter
                }
            } else {
                ltPassengersUserSide.isVisible = true
                ltDriverSidePassengers.isVisible = false
                tvMyDetailTitle.isVisible = false

                includePassengersInfo.apply {
                    rvPassengers.apply {
                        this.setRVHorizontal()
                        adapter = passengersAdapter
                    }
                }
            }
        }
    }

    private fun startMessaging(user: User?) {
        user?.let {
            startActivity(
                Intent(this, MessageActivity::class.java).apply {
                    putExtra(
                        MESSAGE_USER_INFO, Users(
                            _id = it._id,
                            name = it.name,
                            photo = it.photo
                        )
                    )
                }
            )
        }
    }

    private fun startCall(user: User?) {
        val uri = "tel:" + user?.phoneNumber
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse(uri))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        getTripDetailsViewModel.getTrips(tripsID = tripID)
        bindGetTripDetailsObserver()
    }

    @SuppressLint("SetTextI18n", "RepeatOnLifecycleWrongUsage")
    private fun bindGetTripDetailsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getTripDetailsViewModel.getTripDetailsState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            Log.e(
                                TAG, "bindGetTripDetailsObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            it.data.data?.let { tripsData ->
                                setupViews(tripsData)
                                skeleton.showOriginal()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews(tripsData: TripsData) {
        binding.apply {
            if (sharedPreferenceManager.driverMode()) {
                // Data handling on driver side
                // Passengers
                tvSeatsLeft.text = "${checkLongValue(tripsData.trip?.availableSeats)} Seats Left"
                if (tripsData.bookings?.isEmpty() == true || tripsData.bookings == null) {
//                    tvDriverSidePassengersNotAvailable.isVisible = true
//                    rvDriverSidePassengers.isVisible = false
                    ltDriverSidePassengers.isVisible = false
                } else {
//                    tvDriverSidePassengersNotAvailable.isVisible = false
//                    rvDriverSidePassengers.isVisible = true
                    ltDriverSidePassengers.isVisible = true
                    driverSidePassengersAdapter.submitList(tripsData.bookings)
                }

                // My Details
                tripsData.trip?.driver.let { driver ->
                    includeUserDetailLayout.apply {
                        ltCallInboxSection.isVisible = false
                        loadProfileImage(imgUserProfile, driver?.photo)
                        tvUserName.text = checkStringValue(this@TripDetailActivity, driver?.name)

                        driver?.reviewsStats.let { review ->
                            tvAvgRating.text = checkFloatValue(review?.avgRating)
                            tvTotalReviews.text = "(${
                                checkLongValue(review?.totalReviews)
                            })"
                        }

                        driver?.carDetails.let { car ->
                            includeVehicleInfoLayout.apply {
                                loadImage(imgVehicleProfile, car?.photo)
                                tvVehicleModel.text = checkStringValue(
                                    this@TripDetailActivity, car?.model
                                )
                                tvVehicleYear.text = checkLongValue(car?.year)
                                tvVehicleColor.text = checkStringValue(
                                    this@TripDetailActivity, car?.color
                                )

                                car?.reviewsStats.let { review ->
                                    tvVehicleAvgRating.text = checkFloatValue(review?.avgRating)
                                    tvVehicleTotalReviews.text = "(${
                                        checkLongValue(review?.totalReviews)
                                    })"
                                }

                                tvVehicleRegistrationNumber.text = checkStringValue(
                                    this@TripDetailActivity, car?.registrationNumber
                                )
                            }
                        }
                    }
                }

                tripsData.trip.let { trip ->
                    Log.e(TAG, "setupViews: trip -> $trip")
                    // Destination and Schedule
                    includeDestinationAndScheduleLayout.apply {
                        includeTripRouteLayout.apply {
                            tvAddressFrom.text = checkStringValue(
                                this@TripDetailActivity, trip?.from_address
                            )
                            formatDateTime(
                                this@TripDetailActivity, tvDepartureDate, trip?.departureDate
                            )
                            tvAddressWhereto.text = checkStringValue(
                                this@TripDetailActivity, trip?.whereTo_address
                            )
                        }

                        tvPricePerPerson.text = checkPriceValue(trip?.pricePerPerson)
                    }

                    // Trip Details
                    includeTripDetailLayout.apply {
                        trip?.luggageRestrictions?.let { luggageRestrictions ->
                            tvTypeValue.text = checkStringValue(
                                this@TripDetailActivity, luggageRestrictions.text
                            )
                            tvWeightValue.text = "${
                                checkLongValue(luggageRestrictions.weight)
                            }$WEIGHT_SIGN"
                        }
                        tvRoundTripValue.text = if (trip?.roundTrip == true) {
                            "Yes"
                        } else {
                            "No"
                        }
                        tvSmokingAllowedValue.text = if (trip?.smokingPreference == true) {
                            "Yes"
                        } else {
                            "No"
                        }
                        tvLanguagePreferenceValue.text = checkStringValue(
                            this@TripDetailActivity, trip?.languagePreference
                        )
                        tvOtherRelevantDetailsValue.text = checkStringValue(
                            this@TripDetailActivity, trip?.note
                        )
                    }
                }

                imgHeart.isVisible = false

                btnTripEnd.text = ContextCompat.getString(
                    this@TripDetailActivity, R.string.end_trip
                )

                when (tripsData.trip?.status) {
                    SCHEDULED -> {
                        btnTripEnd.isVisible = false
                        ltCancelStartTrip.isVisible = true
                    }

                    IN_PROGRESS -> {
                        btnTripEnd.isVisible = true
                        ltCancelStartTrip.isVisible = false
                    }

                    CANCELED -> {

                    }
                }

                btnTripEnd.setOnClickListener {
                    isScreenNeedToFinish = true
                    updateTripStatusViewModel.updateTripStatus(tripID, COMPLETED)
                    bindUpdateTripStatusObserver(
                        TRIP_COMPLETED_TITLE,
                        TRIP_COMPLETED_BODY,
                        tripsData.bookings
                    )

                    sendNotification(TRIP_COMPLETED_TITLE, TRIP_COMPLETED_BODY, tripsData.bookings)
                }

                btnStartTrip.setOnClickListener {
                    updateTripStatusViewModel.updateTripStatus(tripID, IN_PROGRESS)
                    bindUpdateTripStatusObserver(
                        START_BOOKING_TITLE,
                        START_BOOKING_BODY,
                        tripsData.bookings
                    )

                    sendNotification(START_BOOKING_TITLE, START_BOOKING_BODY, tripsData.bookings)
                }

                btnCancel.setOnClickListener {
                    isScreenNeedToFinish = true
                    updateTripStatusViewModel.updateTripStatus(tripID, CANCELED)
                    bindUpdateTripStatusObserver(
                        BOOKED_CANCELLED_TITLE,
                        BOOKED_CANCELLED_BODY,
                        tripsData.bookings
                    )

                    sendNotification(
                        BOOKED_CANCELLED_TITLE,
                        BOOKED_CANCELLED_BODY,
                        tripsData.bookings
                    )
                }
            } else {
                // Data handling on user side
                tripsData.driver.let { driver ->
                    Log.e(TAG, "setupViews: driver -> $driver")
                    includeUserDetailLayout.apply {
                        ltCallInboxSection.isVisible = false
                        loadProfileImage(imgUserProfile, driver?.photo)
                        tvUserName.text = checkStringValue(
                            this@TripDetailActivity, driver?.name
                        )

                        driver?.reviewsStats.let { review ->
                            tvAvgRating.text = checkFloatValue(review?.avgRating)
                            tvTotalReviews.text = "(${checkLongValue(review?.totalReviews)})"
                        }

                        driver?.carDetails.let { car ->
                            includeVehicleInfoLayout.apply {
                                loadImage(imgVehicleProfile, car?.photo)
                                tvVehicleModel.text = checkStringValue(
                                    this@TripDetailActivity, car?.model
                                )
                                tvVehicleYear.text = checkLongValue(car?.year)
                                tvVehicleColor.text = checkStringValue(
                                    this@TripDetailActivity, car?.color
                                )

                                car?.reviewsStats.let { review ->
                                    tvVehicleAvgRating.text = checkFloatValue(review?.avgRating)
                                    tvVehicleTotalReviews.text = "(${
                                        checkLongValue(review?.totalReviews)
                                    })"
                                }

                                tvVehicleRegistrationNumber.text = checkStringValue(
                                    this@TripDetailActivity, car?.registrationNumber
                                )
                            }
                        }
                    }
                }

                tvPassengersAvailableSeat.text = "${
                    checkLongValue(tripsData.availableSeats)
                } Seats Available"
                if (tripsData.passengers.isNullOrEmpty()) {
                    includePassengersInfo.apply {
                        tvPassengersNotAvailable.isVisible = true
                        rvPassengers.isVisible = false
                    }
                } else {
                    includePassengersInfo.apply {
                        tvPassengersNotAvailable.isVisible = false
                        rvPassengers.isVisible = true
                    }
                    passengersAdapter.submitList(tripsData.passengers)
                }

                // Destination and Schedule
                includeDestinationAndScheduleLayout.apply {
                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            this@TripDetailActivity, tripsData.from_address
                        )
                        formatDateTime(
                            this@TripDetailActivity, tvDepartureDate, tripsData.departureDate
                        )
                        tvAddressWhereto.text = checkStringValue(
                            this@TripDetailActivity, tripsData.whereTo_address
                        )
                    }

                    tvPricePerPerson.text = checkPriceValue(tripsData.pricePerPerson)
                }

                // Trip Details
                includeTripDetailLayout.apply {
                    tvLanguagePreferenceValue.text = checkStringValue(
                        this@TripDetailActivity, tripsData.languagePreference
                    )
                    tvTypeValue.text = checkStringValue(
                        this@TripDetailActivity, tripsData.luggageRestrictions?.text
                    )
                    tvWeightValue.text = "${
                        checkLongValue(
                            tripsData.luggageRestrictions?.weight
                        )
                    }$WEIGHT_SIGN"
                    tvRoundTripValue.text = if (tripsData.roundTrip) {
                        "Yes"
                    } else {
                        "No"
                    }
                    if (tripsData.smokingPreference) {
                        tvSmokingAllowedValue.text = "Yes"
                    } else {
                        tvSmokingAllowedValue.text = "No"
                    }
                    tvLanguagePreferenceValue.text = checkStringValue(
                        this@TripDetailActivity, tripsData.languagePreference
                    )
                    tvOtherRelevantDetailsValue.text = checkStringValue(
                        this@TripDetailActivity, tripsData.note
                    )
                }

                imgHeart.isVisible = true
                btnBookNow.isVisible = true
                ltCancelStartTrip.isVisible = false

                Log.e(TAG, "setupViews: status ----> ${tripsData.status}")

                when (tripsData.status) {
                    SCHEDULED -> {
                        ltButtonSection.isVisible = true
                    }

                    IN_PROGRESS -> {
                        ltButtonSection.isVisible = false
                    }
                }

                btnBookNow.text = ContextCompat.getString(
                    this@TripDetailActivity, R.string.book_now
                )

                val intent = Intent()
                if (sharedPreferenceManager.driverMode()) {
                        imgHeart.isVisible = false
                        imgRedHeart.isVisible = false
                } else {
                    if (tripsData.isAddedInWishList) {
                        imgHeart.isVisible = false
                        imgRedHeart.isVisible = true
                    } else {
                        imgHeart.isVisible = true
                        imgRedHeart.isVisible = false
                    }
                }



                imgHeart.setOnClickListener {
                    imgHeart.isVisible = false
                    imgRedHeart.isVisible = true

                    tripsData.isAddedInWishList = true
                    intent.putExtra(TRIP_ID, tripsData._id)
                    // Pass the current favorite status
                    intent.putExtra(INTENT_IS_TRIP_WISH_LISTED, tripsData.isAddedInWishList)
                    setResult(WISH_LIST_CHECKER_CODE, intent)

                    addToWishList(tripsData._id)
                }

                imgRedHeart.setOnClickListener {
                    imgHeart.isVisible = true
                    imgRedHeart.isVisible = false

                    tripsData.isAddedInWishList = false
                    intent.putExtra(TRIP_ID, tripsData._id)
                    // Pass the current favorite status
                    intent.putExtra(INTENT_IS_TRIP_WISH_LISTED, tripsData.isAddedInWishList)
                    setResult(WISH_LIST_CHECKER_CODE, intent)

                    addToWishList(tripsData._id)
                }

                btnBookNow.setOnClickListener {
                    Log.e(TAG, "setupViews: tripID -> $tripID")
                    if (tripsData.availableSeats!! <= 0) {
                        Toast(this@TripDetailActivity).showSuccessMessage(
                            this@TripDetailActivity, "No seats available"
                        )
                    } else {
                        startActivity(Intent(
                            this@TripDetailActivity, BookNowActivity::class.java
                        ).apply {
                            putExtra(TRIP_ID, tripID)
                            putExtra(GET_TRIP_DETAIL, tripsData)
                        })
                    }
                }
            }
        }
    }

    private fun addToWishList(id: String) {
        addToWishListViewModel.addToWishList(id)
        binAddToWishListObserver()
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
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

    private fun bindUpdateTripStatusObserver(
        title: String? = null,
        body: String? = null,
        toId: List<Booking>? = null
    ) {
        lifecycleScope.launch {
            updateTripStatusViewModel.updateTripStatusState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@TripDetailActivity).showErrorMessage(
                            this@TripDetailActivity, it.message.toString()
                        )

                        Log.e(
                            TAG, "bindMarkTripCompletedObserver: Error -> " + it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@TripDetailActivity).showSuccessMessage(
                            this@TripDetailActivity, it.data.message.toString()
                        )

                        if (it.data.message == "Update trip status to In Progress") {
                            binding.apply {
                                btnTripEnd.isVisible = true
                                ltCancelStartTrip.isVisible = false
                            }
                        }

                        Log.e(
                            TAG,
                            "bindMarkTripCompletedObserver: success -> " + it.data.message.toString()
                        )

//                        if (toId?.isEmpty() == false || toId != null) {
////                            sendNotification(title.toString(), body.toString(), toId)
//                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(title: String, body: String, toId: List<Booking>?) {
        Log.e(TAG, "sendNotification: title -> $title")
        Log.e(TAG, "sendNotification: body -> $body")
        Log.e(TAG, "sendNotification: body -> $body")
        toId?.let {
            for (topicId in it) {
                pushNotificationViewModel.sendMessageNotification(
                    NotificationRequest(
                        notification = NotificationRequest.Notification(
                            title = title,
                            body = "$body ${authModelInfo?.name}.",
                            mutable_content = MUTABLE_CONTENT,
                            sound = TONE
                        ),
                        to = "${TOPIC}${TROOUTE_TOPIC}${topicId.user?._id.toString()}"
                    )
                )
            }

            bindSendMessageNotificationObserver()
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

                    if (isScreenNeedToFinish) {
                        isScreenNeedToFinish = true
                        finish()
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}