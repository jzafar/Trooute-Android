package com.example.trooute.presentation.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants.CREATE_BOOKING_REQUEST
import com.example.trooute.core.util.Constants.GET_TRIP_DETAIL
import com.example.trooute.core.util.Constants.MUTABLE_CONTENT
import com.example.trooute.core.util.Constants.PLATFORM_FEE_PRICE
import com.example.trooute.core.util.Constants.PRICE_SIGN
import com.example.trooute.core.util.Constants.TONE
import com.example.trooute.core.util.Constants.TOPIC
import com.example.trooute.core.util.Constants.TRIP_BOOKED_BODY
import com.example.trooute.core.util.Constants.TRIP_BOOKED_TITLE
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.bookings.request.CreateBookingRequest
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivityConfirmBookingBinding
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.bookingviewmodel.CreateBookingViewModel
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmBookingActivity : AppCompatActivity() {

    private val TAG = "ConfirmBooking"

    private lateinit var binding: ActivityConfirmBookingBinding

    private var authModelInfo: User? = null

    private var platFormFee:Double = 1.0

    private val tripsData: TripsData? by lazy {
        IntentCompat.getParcelableExtra(intent, GET_TRIP_DETAIL, TripsData::class.java)
    }

    private val createBookingRequest: CreateBookingRequest? by lazy {
        IntentCompat.getParcelableExtra(
            intent, CREATE_BOOKING_REQUEST, CreateBookingRequest::class.java
        )
    }

    private val createBookingViewModel: CreateBookingViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var loader: Loader

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_booking)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Confirm Booking"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            authModelInfo = sharedPreferenceManager.getAuthModelFromPref()

            tripsData?.let { trips ->
                includeBookingDetailLayout.apply {
                    tvAvailableSeat.text = "${checkLongValue(trips.availableSeats)} Seats Available"

                    includeUserInfo.apply {
                        loadProfileImage(imgUserProfile, trips.driver?.photo)
                        tvUserName.text = checkStringValue(
                            this@ConfirmBookingActivity, trips.driver?.name
                        )
                        tvAvgRating.text = checkFloatValue(trips.driver?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(trips.driver?.reviewsStats?.totalReviews)
                        })"

                        loadImage(imgCarImage, trips.driver?.carDetails?.photo)
                        tvCarModel.text = checkStringValue(
                            this@ConfirmBookingActivity, trips.driver?.carDetails?.model
                        )
                        tvCarRegistrationNumber.text = checkStringValue(
                            this@ConfirmBookingActivity,
                            trips.driver?.carDetails?.registrationNumber
                        )
                    }

                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            this@ConfirmBookingActivity, trips.from_address
                        )
                        formatDateTime(
                            this@ConfirmBookingActivity,
                            tvDepartureDate,
                            trips.departureDate
                        )
                        tvAddressWhereto.text = checkStringValue(
                            this@ConfirmBookingActivity, trips.whereTo_address
                        )
                    }

                    tvNxSeats.text = checkNumOfSeatsValue(createBookingRequest?.numberOfSeats)
                    tvNxSeatsPrice.text = checkPriceValue(createBookingRequest?.amount)

                    platFormFee = PLATFORM_FEE_PRICE * createBookingRequest?.numberOfSeats!!

                    tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                    tvTotalPrice.text = checkPriceValue(
                        createBookingRequest?.amount?.plus(platFormFee)
                    )
                }

                tvTotalAmount.text = checkPriceValue(
                    createBookingRequest?.amount?.plus(platFormFee)
                )
            }

            btnBookNow.setOnClickListener {
                createBookingRequest?.let { createBooking ->
                    createBookingViewModel.createBooking(
                        CreateBookingRequest(
                            amount = createBooking.amount?.plus(platFormFee),
                            note = createBooking.note,
                            numberOfSeats = createBooking.numberOfSeats,
                            pickupLocation = createBooking.pickupLocation?.location?.let { location ->
                                CreateBookingRequest.PickupLocation(
                                    address = createBooking.pickupLocation.address,
                                    location = location
                                )
                            },
                            trip = createBooking.trip,
                            plateFormFee = platFormFee
                        )
                    )
                }

                bindCreateBookingObserver()
            }
        }
    }

    private fun bindCreateBookingObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                createBookingViewModel.createBookingState.collect {
                    loader.cancel()
                    when (it) {
                        is Resource.ERROR -> {
                            Toast(this@ConfirmBookingActivity).showErrorMessage(
                                this@ConfirmBookingActivity, it.message.toString()
                            )
                            Log.e(
                                TAG, "bindCreateBookingObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {
                            loader.show()
                        }

                        is Resource.SUCCESS -> {
                            Toast(this@ConfirmBookingActivity).showSuccessMessage(
                                this@ConfirmBookingActivity, it.data.message.toString()
                            )
                            Log.e(TAG, "bindCreateBookingObserver: Success -> " + it.data)

                            sendNotification()
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification() {
        Log.e(TAG, "sendNotification: ${tripsData?.driver?._id.toString()}")
        Log.e(TAG, "sendNotification: Called")
        pushNotificationViewModel.sendMessageNotification(
            NotificationRequest(
                notification = NotificationRequest.Notification(
                    title = TRIP_BOOKED_TITLE,
                    body = "$TRIP_BOOKED_BODY${authModelInfo?.name}.",
                    mutable_content = MUTABLE_CONTENT,
                    sound = TONE
                ),
                to = "${TOPIC}${TROOUTE_TOPIC}${
                    tripsData?.driver?._id.toString()
                }"
            )
        )

        bindSendMessageNotificationObserver()
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

                    startActivity(Intent(
                        this@ConfirmBookingActivity,
                        BookingConfirmedActivity::class.java
                    ).apply {
                        putExtra(GET_TRIP_DETAIL, tripsData)
                        putExtra(CREATE_BOOKING_REQUEST, createBookingRequest)
                    })
                }
            }
        }.launchIn(lifecycleScope)
    }
}