package com.travel.trooute.presentation.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.ACCEPT_BOOKING_BODY
import com.travel.trooute.core.util.Constants.ACCEPT_BOOKING_TITLE
import com.travel.trooute.core.util.Constants.BOOKED_CANCELLED_BODY
import com.travel.trooute.core.util.Constants.BOOKED_CANCELLED_TITLE
import com.travel.trooute.core.util.Constants.BOOKING_ID
import com.travel.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.travel.trooute.core.util.Constants.MUTABLE_CONTENT
import com.travel.trooute.core.util.Constants.PLATFORM_FEE_PRICE
import com.travel.trooute.core.util.Constants.PRICE_SIGN
import com.travel.trooute.core.util.Constants.TONE
import com.travel.trooute.core.util.Constants.TOPIC
import com.travel.trooute.core.util.Constants.TROOUTE_TOPIC
import com.travel.trooute.core.util.Constants.USER_ID
import com.travel.trooute.core.util.Constants.WEIGHT_SIGN
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.data.model.bookings.response.BookingDetailsData
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.data.model.common.Passenger
import com.travel.trooute.data.model.notification.NotificationRequest
import com.travel.trooute.data.model.review.request.CreateReviewRequest
import com.travel.trooute.databinding.ActivityBookingDetailBinding
import com.travel.trooute.presentation.adapters.PassengersPrimaryAdapter
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.ui.chat.MessageActivity
import com.travel.trooute.presentation.ui.main.MakePaymentActivity
import com.travel.trooute.presentation.ui.review.ReviewsActivity
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.Utils.formatDateTime
import com.travel.trooute.presentation.utils.Utils.getSubString
import com.travel.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.travel.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.ValueChecker.itOrNull
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.loadImage
import com.travel.trooute.presentation.utils.loadProfileImage
import com.travel.trooute.presentation.utils.setRVHorizontal
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.bookingviewmodel.GetBookingDetailsViewModel
import com.travel.trooute.presentation.viewmodel.bookingviewmodel.ProcessBookingViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.travel.trooute.presentation.viewmodel.reviewviewmodel.CreateReviewViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.internal.ViewUtils
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.PickupStarted
import com.travel.trooute.core.util.Constants.SCHEDULED
import com.travel.trooute.data.model.Enums.PickUpPassengersStatus
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.data.model.trip.response.Booking
import com.travel.trooute.data.model.trip.response.LuggageType
import com.travel.trooute.data.model.trip.response.TripsData
import com.travel.trooute.presentation.ui.trip.PickupPassengersActivity
import com.travel.trooute.presentation.utils.ValueChecker.checkLuggageRestrictionValue
import com.travel.trooute.presentation.viewmodel.tripviewmodel.GetPickupPassengersViewModel
import com.travel.trooute.presentation.viewmodel.tripviewmodel.UpdatePickupStatusViewModel
import com.travel.trooute.presentation.viewmodel.tripviewmodel.UpdateTripStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BookingDetailActivity : AppCompatActivity() , AdapterItemClickListener {

    private val TAG = "BookingDetail"

    private lateinit var binding: ActivityBookingDetailBinding
    private lateinit var bookingId: String
    private lateinit var skeleton: Skeleton

    private var authModelInfo: User? = null
    private var currentBooking: Booking? = null

    private val getBookingDetailsViewModel: GetBookingDetailsViewModel by viewModels()
    private val processBookingViewModel: ProcessBookingViewModel by viewModels()
    private val createReviewViewModel: CreateReviewViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()
    private val getPickupPassengersViewModel: GetPickupPassengersViewModel by viewModels()
    private val updatePickUpPassengersStatus: UpdatePickupStatusViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail)
        bookingId = intent.getStringExtra(BOOKING_ID).toString()
        authModelInfo = sharedPreferenceManager.getAuthModelFromPref()

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getString(R.string.booking_details)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = ltRoot.createSkeleton()
            skeleton.showSkeleton()

            getBookingDetailsViewModel.getBookingDetails(bookingId = bookingId)
            bindGetBookingDetailsObserver()
            includePickupStatusLayout.apply {
                btnNotShowedUp.setOnClickListener {
                    currentBooking?.let { it1 -> onUpdateStatusButtonClick(data = it1, status = PickUpPassengersStatus.DriverNotShowedup) }
                }

                btnMarkedAsPickedUp.setOnClickListener {
                    currentBooking?.let { it1 -> onUpdateStatusButtonClick(data = it1, status = PickUpPassengersStatus.DriverPickedup) }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindGetBookingDetailsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getBookingDetailsViewModel.getBookingDetailsState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            Log.e(
                                TAG,
                                "bindGetBookingDetailsObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.i(TAG, "bindGetBookingDetailsObserver: Success -> " + it.data)
                            it.data.data?.let { bookingData ->
                                setUpBookingDetailViews(bookingData)

                                if (sharedPreferenceManager.driverMode()) {
                                    binding.apply {
                                        ltUserSideReview.isVisible = false
                                        ltDriverSidePassengers.isVisible = true
                                        ltUserSideDriverDetails.isVisible = false
                                        ltUserSidePassengers.isVisible = false
                                        ltUserSideDesignationSchedule.isVisible = false
                                        ltUserSideTripDetails.isVisible = false
                                        ltDriverSidePickupLocation.isVisible = true
                                    }
                                    setUpDriverSidePassengersDetailsViews(bookingData)
                                    setUpDriverSidePickupLocationViews(bookingData)
                                } else {
                                    binding.apply {
                                        ltUserSideReview.isVisible = true
                                        ltDriverSidePassengers.isVisible = false
                                        ltUserSideDriverDetails.isVisible = true
                                        ltUserSidePassengers.isVisible = true
                                        ltUserSideDesignationSchedule.isVisible = true
                                        ltUserSideTripDetails.isVisible = true
                                        ltDriverSidePickupLocation.isVisible = true
                                    }
                                    setUpUserSideReviewViews(bookingData)
                                    setUpUserSideDriverDetailsViews(bookingData)
                                    setUpUserSidePassengersViews(bookingData)
                                    setUpUserSideDesignationAndScheduleViews(bookingData)
                                    setUpUserSideTripDetailsViews(bookingData)
                                    setUpDriverSidePickupLocationViews(bookingData)
//                                    showPassengerPickupStatusButton(bookingData)
                                }
                            }

                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

//    private fun showPassengerPickupStatusButton(bookingData: BookingDetailsData){
//        if (bookingData.status == getString(R.string.confirmed)) {
//            binding.apply {
//                includeAppBar.apply {
//                    this.filter.isVisible = true
//                    filter.setOnClickListener {
//                        startActivity(Intent(
//                            this@BookingDetailActivity, PickupPassengersActivity::class.java
//                        ).apply {
//                            putExtra(Constants.TRIP_ID, bookingData.trip?._id)
//                        })
//                    }
//                }
//            }
//        }
//
//    }

    @SuppressLint("SetTextI18n")
    private fun setUpBookingDetailViews(bookingData: BookingDetailsData) {
        binding.apply {
            when (bookingData.status) {
                "Waiting" -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = true
                    includeCancelledLayout.mcCancelledBooking.isVisible = false
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = false

                    includeWaitingLayout.apply {
                        if (sharedPreferenceManager.driverMode()) {
                            ltCancelUserSide.isVisible = false
                            ltCancelAccept.isVisible = true
                            tvStatus.text = ContextCompat.getString(
                                this@BookingDetailActivity, R.string.waiting_for_approval
                            )
                        } else {
                            ltCancelUserSide.isVisible = true
                            ltCancelAccept.isVisible = false
                            tvStatus.text = ContextCompat.getString(
                                this@BookingDetailActivity, R.string.waiting
                            )
                        }
                        tvBookingId.text = getString(R.string.booking) + " # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

                        if (sharedPreferenceManager.driverMode()) {
                            ltPlatformFee.isVisible = true
                            ltCancelUserSide.isVisible = false
                            ltCancelAccept.isVisible = true
                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                            tvTotalPrice.text = checkPriceValue(
                                pricePerSeat - platFormFee
                            )

                            btnCancelBooking.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.user?._id.toString()
                                )
                            }

                            btnAccept.setOnClickListener {

                                if (bookingData.trip?.driver?.stripeConnectedAccountId == null) {
                                    showConnectStripeAccountAlert()
                                    return@setOnClickListener
                                }
                                val numberOfRequestSeats = bookingData.numberOfSeats
                                val remainingSeats = bookingData.trip?.availableSeats
                                if (remainingSeats != null){
                                    if (numberOfRequestSeats > remainingSeats) {
                                        showNumberOfSeatsAlertDialog()
                                        return@setOnClickListener
                                    }
                                }

                                processBookingViewModel.approveBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    ACCEPT_BOOKING_TITLE,
                                    ACCEPT_BOOKING_BODY,
                                    bookingData.user?._id.toString()
                                )
                            }
                        } else {
                            ltPlatformFee.isVisible = true

                            val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!

                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                            ltCancelUserSide.isVisible = true
                            ltCancelAccept.isVisible = false

                            tvTotalPrice.text = checkPriceValue(
                                bookingData.amount
                            )

                            btnCancelBookingUserSide.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.trip?.driver?._id.toString()
                                )
                            }
                        }
                    }
                }

                "Confirmed" -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = false
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = true
                    includeCompletedLayout.mcCompletedBooking.isVisible = false
                    includeUserDetailLayout.apply {
                        ltCallInboxSection.isVisible = true
                    }

                    includeConfirmedLayout.apply {
                        tvStatus.text = checkStringValue(
                            this@BookingDetailActivity, bookingData.status
                        )
                        tvBookingId.text = getString(R.string.booking) + " # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )
                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

                        ltPlatformFee.isVisible = true
                        tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"

                        if (sharedPreferenceManager.driverMode()) {
                            tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)
                            btnCancelBooking.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.user?._id.toString()
                                )
                            }
                        } else {
                            tvTotalPrice.text = checkPriceValue(bookingData.amount)

                            btnCancelBooking.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.trip?.driver?._id.toString()
                                )
                            }
                        }
                    }
                }

                "Canceled" -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = true
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = false

                    includeCancelledLayout.apply {
                        tvStatus.text = ContextCompat.getString(
                            this@BookingDetailActivity, R.string.cancelled
                        )
                        tvBookingId.text = getString(R.string.booking) + " # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(bookingData.amount)

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)
                        ltPlatformFee.isVisible = true
                        tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                        if (sharedPreferenceManager.driverMode()) {
                            tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)
                        } else {

                            tvTotalPrice.text = checkPriceValue(
                                pricePerSeat + platFormFee
                            )
                        }
                    }
                }

                "Approved" -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = false
                    includeApprovedLayout.mcApprovedBooking.isVisible = true
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = false

                    includeApprovedLayout.apply {
                        tvStatus.text = if (sharedPreferenceManager.driverMode()) {
                            ContextCompat.getString(
                                this@BookingDetailActivity, R.string.waiting_for_payment
                            )
                        } else {
                            ContextCompat.getString(
                                this@BookingDetailActivity, R.string.approved
                            )
                        }
                        tvBookingId.text = getString(R.string.booking) + " # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

                        ltPlatformFee.isVisible = true
                        tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                        if (sharedPreferenceManager.driverMode()) {
                            ltDriverSideButton.isVisible = true
                            ltUserSideButtons.isVisible = false

                            tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)

                            btnCancelBookingDriverSide.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.user?._id.toString()
                                )
                            }
                        } else {
                            ltDriverSideButton.isVisible = false
                            ltUserSideButtons.isVisible = true

                            tvTotalPrice.text = checkPriceValue(
                                pricePerSeat + platFormFee
                            )

                            btnCancelBooking.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.trip?.driver?._id.toString()
                                )
                            }

                            btnMakePayment.setOnClickListener {
                                Log.e(TAG, "setUpBookingDetailViews: driver id 1 -> " + bookingData.trip?.driver?._id.toString())
                                sharedPreferenceManager.saveMakePaymentUserId(bookingData.trip?.driver?._id.toString())
                                Log.e(TAG, "setUpBookingDetailViews: driver id 2 -> " + sharedPreferenceManager.getMakePaymentUserIdFromPref())
                                processBookingViewModel.confirmBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver()
                            }
                        }
                    }
                }

                "Completed" -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = false
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = true

                    includeCompletedLayout.apply {
                        tvStatus.text = checkStringValue(
                            this@BookingDetailActivity, bookingData.status
                        )
                        tvBookingId.text = getString(R.string.booking) + " # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

                        ltPlatformFee.isVisible = true
                        tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"

                        if (sharedPreferenceManager.driverMode()) {
                            tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)
                        } else {

                            tvTotalPrice.text = checkPriceValue(
                                platFormFee + pricePerSeat
                            )
                        }
                    }
                }
            }

            if (!sharedPreferenceManager.driverMode()) {
                if (bookingData.trip?.status == SCHEDULED) {
                    pickUpStatusView.isVisible = false
                } else {
                    if(bookingData.trip?.status == PickupStarted && bookingData.status == "Confirmed") {
                        val mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post(object : Runnable {
                            override fun run() {
                                getPickupStatus(bookingData.trip._id)
                                mainHandler.postDelayed(this, 5000)
                            }
                        })
                        bindGetPickupStatusObserver()
                    }

                }

            }

        }
    }
    @SuppressLint("RestrictedApi")
    private fun setUpViewForPassengerSidePickupStatus(bookingData: Booking){
        currentBooking = bookingData
        binding.apply {
            pickUpStatusView.isVisible = true
            when (bookingData.pickupStatus?.driverStatus) {
                PickUpPassengersStatus.NotStarted.toString() -> {
                    pickUpStatusView.isVisible = false
                }
                PickUpPassengersStatus.PickupStarted.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false

                    }
                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.pickup_started)
                        statusDetail.text = getString(R.string.pickup_started_passenger_details)
                    }
                }
                PickUpPassengersStatus.PassengerNotified.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false
                    }
                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.get_ready)
                        statusDetail.text = getString(R.string.get_ready_passenger_details)
                    }
                }
                PickUpPassengersStatus.PassengerPickedup.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false
                    }
                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.pickedup)
                        statusDetail.text = getString(R.string.pick_up_passenger_details)
                    }
                }
                PickUpPassengersStatus.PassengerNotShowedup.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false
                    }
                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.not_showed_up)
                        statusDetail.text = getString(R.string.not_showed_up_passenger_details)
                    }
                }
            }

            when (bookingData.pickupStatus?.passengerStatus) {
                // set by passenger for driver
                PickUpPassengersStatus.DriverPickedup.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false
                    }
                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.pickedup)
                        statusDetail.text = getString(R.string.pick_up_driver_details)
                        ltCancelAccept.isVisible = false
                        TextViewBindingAdapter.setDrawableStart(
                            tvStatus,
                            ContextCompat.getDrawable(this@BookingDetailActivity, R.drawable.ic_confirm_check)
                        )
                    }

                }
                // set by passenger for driver
                PickUpPassengersStatus.DriverNotShowedup.toString() -> {
                    pickUpStatusView.isVisible = true
                    includeConfirmedLayout.apply {
                        btnCancelBooking.isVisible = false
                    }

                    includePickupStatusLayout.apply {
                        tvStatus.text = getString(R.string.not_showed_up)
                        statusDetail.text = getString(R.string.not_showed_up_driver_details)
                        TextViewBindingAdapter.setDrawableStart(
                            tvStatus,
                            ContextCompat.getDrawable(this@BookingDetailActivity, R.drawable.ic_status_cancelled)
                        )
                    }
                }
            }
        }

    }
    private fun bindGetPickupStatusObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getPickupPassengersViewModel.getPickupState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(
                                TAG, "bindGetPickupStatusObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {}

                        is Resource.SUCCESS -> {
                            it.data.data?.let { tripsData ->
                                val booking =
                                    tripsData.bookings?.single { booking -> booking._id == bookingId }
                                if (booking != null) {
                                    setUpViewForPassengerSidePickupStatus(booking)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getPickupStatus(tripID: String?) {
        tripID?.let { getPickupPassengersViewModel.getPickUpStatus(it) }
    }

    private fun showConnectStripeAccountAlert() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.connect_stripe))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->

            }
            .create()
            .show()
    }

    private fun showNumberOfSeatsAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.less_remaining_seats))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->

            }
            .create()
            .show()
    }
    private fun bindProcessBookingObserver(
        title: String? = null,
        body: String? = null,
        toId: String? = null
    ) {
        lifecycleScope.launch {
            processBookingViewModel.processBookingState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@BookingDetailActivity).showErrorMessage(
                            this@BookingDetailActivity, it.message.toString()
                        )
                        Log.e(TAG, "bindProcessBookingObserver: Error -> " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Log.e(
                            TAG, "bindProcessBookingObserver: Success -> " + it.data
                        )

                        if (it.data.message?.trim()?.lowercase() == "payment session created.") {
                            startActivity(
                                Intent(
                                    this@BookingDetailActivity,
                                    MakePaymentActivity::class.java
                                ).apply {
                                    putExtra("PaymentIntegrationUrl", it.data.url)
                                })
                        } else {
                            sendNotification(title.toString(), body.toString(), toId.toString())
                            Toast(this@BookingDetailActivity).showSuccessMessage(
                                this@BookingDetailActivity, it.data.message.toString()
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(title: String, body: String, toId: String) {
        Log.e(TAG, "sendNotification: title -> $title")
        Log.e(TAG, "sendNotification: body -> $body")

        pushNotificationViewModel.sendMessageNotification(
            NotificationRequest(
                notification = NotificationRequest.Notification(
                    title = title,
                    body = "$body ${authModelInfo?.name}.",
                    mutable_content = MUTABLE_CONTENT,
                    sound = TONE
                ),
                to = "${TOPIC}${TROOUTE_TOPIC}${toId}"
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
                }
            }
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpDriverSidePassengersDetailsViews(bookingData: BookingDetailsData) {
        binding.apply {
            includeDriverSideUserDetailLayout.apply {
                loadProfileImage(imgUserProfile, bookingData.user?.photo.toString())
                tvUserName.text = checkStringValue(
                    this@BookingDetailActivity, bookingData.user?.name
                )

                var genderStr = checkStringValue(
                    this@BookingDetailActivity, bookingData.user?.gender
                )

                if (genderStr.equals(getString(R.string.not_provided))){
                    gender.isVisible = false
                }
                else {
                    gender.text = genderStr
                }

                tvAvgRating.text = checkFloatValue(bookingData.user?.reviewsStats?.avgRating)
                tvTotalReviews.text = "(${
                    checkLongValue(
                        bookingData.user?.reviewsStats?.totalReviews
                    )
                })"

                userReviews.setOnClickListener{
                    bookingData.user?.let {
                        startActivity(
                            Intent(this@BookingDetailActivity,
                                ReviewsActivity::class.java).apply {
                                    putExtra(USER_ID, it._id)
                            }
                        )
                    }
                }
                messageIcon.setOnClickListener {
                    bookingData.user?.let {
                        startActivity(
                            Intent(
                                this@BookingDetailActivity,
                                MessageActivity::class.java
                            ).apply {
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

                callIcon.setOnClickListener {
                    bookingData.user?.let {

                        val uri = "tel:" + it.phoneNumber
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.setData(Uri.parse(uri))
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setUpDriverSidePickupLocationViews(bookingData: BookingDetailsData) {
        binding.includePickupLocationDetailLayout.apply {
            tvAddressFrom.text = checkStringValue(
                this@BookingDetailActivity, bookingData.pickupLocation?.address
            )
            tvOtherRelevantDetails.text = checkStringValue(
                this@BookingDetailActivity, bookingData.note
            )
        }
    }

    private fun setUpUserSideReviewViews(bookingData: BookingDetailsData) {
        binding.apply {
            if (bookingData.status == "Completed") {
                ltUserSideReview.isVisible = true
                includeUserSideReviewItemLayout.apply {
                    var rbExperienceWithDriverValue = rbExperienceWithDriver.rating
                    rbExperienceWithDriver.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                        rbExperienceWithDriverValue = rating
                    }

                    var rbRateTheVehicleValue = rbRateTheVehicle.rating
                    rbRateTheVehicle.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                        rbRateTheVehicleValue = rating
                    }

                    bookingData.userToCarReview?.let {
                        lMyReviews.isVisible = true
                        ltWriteReviews.isVisible = false

                        btnSubmitReview.setOnClickListener {
                            if (isFieldValid(etShareYourThoughts, "Review")) {
                                // Review for driver
                                createReviewViewModel.createReview(
                                    CreateReviewRequest(
                                        comment = etShareYourThoughts.text.toString(),
                                        rating = rbExperienceWithDriverValue,
                                        targetId = bookingData.trip?.driver?._id.toString(),
                                        targetType = "Driver",
                                        trip = bookingData.trip?._id.toString(),
                                    )
                                )

                                // Review for car
                                createReviewViewModel.createReview(
                                    CreateReviewRequest(
                                        rating = rbRateTheVehicleValue,
                                        targetId = bookingData.trip?.driver?._id.toString(),
                                        targetType = "Car",
                                        trip = bookingData.trip?._id.toString(),
                                    )
                                )

                                bindCreateReviewObserver()
                            }
                        }
                    }

                    bookingData.userToCarReview?.let { userToCarReview ->
                        ltWriteReviews.isVisible = false
                        lMyReviews.isVisible = true
                        tvMyReviewMessage.text = checkStringValue(
                            this@BookingDetailActivity, userToCarReview.comment
                        )
                    }

                    bookingData.driverToUserReview?.let { driverToUserReview ->
                        ltDriverReview.isVisible = true
                        tvDriverReviewMessage.text = checkStringValue(
                            this@BookingDetailActivity, driverToUserReview.comment
                        )
                    }

                }
            } else {
                ltUserSideReview.isVisible = false
            }
        }
    }

    private fun bindCreateReviewObserver() {
        lifecycleScope.launch {
            createReviewViewModel.createReviewState.collect {
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindCreateReviewObserver: Error -> " + it.message.toString())
                    }

                    Resource.LOADING -> {

                    }

                    is Resource.SUCCESS -> {
                        Log.e(TAG, "bindCreateReviewObserver: success -> " + it.data)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUserSideDriverDetailsViews(bookingData: BookingDetailsData) {
        binding.apply {
            includeUserDetailLayout.apply {
                loadProfileImage(imgUserProfile, bookingData.trip?.driver?.photo)
                tvUserName.text = checkStringValue(
                    this@BookingDetailActivity, bookingData.trip?.driver?.name
                )

                var genderStr = checkStringValue(
                    this@BookingDetailActivity, bookingData.trip?.driver?.gender
                )

                if (genderStr.equals(getString(R.string.not_provided))){
                    gender.isVisible = false
                }
                else {
                    gender.text = genderStr
                }

                tvAvgRating.text = checkFloatValue(
                    bookingData.trip?.driver?.reviewsStats?.avgRating
                )
                tvTotalReviews.text = "(${
                    checkLongValue(bookingData.trip?.driver?.reviewsStats?.totalReviews)
                })"
//                ltCallInboxSection.isVisible = true

                userReviews.setOnClickListener{
                    bookingData.trip?.driver.let {
                        startActivity(
                            Intent(this@BookingDetailActivity,
                                ReviewsActivity::class.java).apply {
                                if (it != null) {
                                    putExtra(USER_ID, it._id)
                                }
                            }
                        )
                    }
                }

                messageIcon.setOnClickListener {
                    bookingData.trip?.driver?.let {
                        startActivity(
                            Intent(
                                this@BookingDetailActivity,
                                MessageActivity::class.java
                            ).apply {
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

                callIcon.setOnClickListener {
                    bookingData.trip?.driver?.let {
                        val uri = "tel:" + it.phoneNumber
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.setData(Uri.parse(uri))
                        startActivity(intent)
                    }
                }
            }

            includeVehicleInfoLayout.apply {
                loadImage(imgVehicleProfile, bookingData.trip?.driver?.carDetails?.photo)
                tvVehicleModel.text = checkStringValue(
                    this@BookingDetailActivity,
                    bookingData.trip?.driver?.carDetails?.make + " " +
                            bookingData.trip?.driver?.carDetails?.model
                )
                tvVehicleYear.text = checkLongValue(
                    bookingData.trip?.driver?.carDetails?.year
                )
                tvVehicleColor.text = checkStringValue(
                    this@BookingDetailActivity, bookingData.trip?.driver?.carDetails?.color
                )
                tvVehicleAvgRating.text = checkFloatValue(
                    bookingData.trip?.driver?.carDetails?.reviewsStats?.avgRating
                )
                tvVehicleTotalReviews.text = "(${
                    checkLongValue(
                        bookingData.trip?.driver?.carDetails?.reviewsStats?.totalReviews
                    )
                })"
                tvVehicleRegistrationNumber.text = checkStringValue(
                    this@BookingDetailActivity,
                    bookingData.trip?.driver?.carDetails?.registrationNumber
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUserSidePassengersViews(bookingData: BookingDetailsData) {
        binding.apply {
            tvUserSidePassengerSeatsAvailable.text = "${
                checkLongValue(bookingData.numberOfSeats)
            } " + getString(R.string.seats_available)
            includePassengersInfoLayout.apply {
                rvPassengers.apply {
                    setRVHorizontal()
                    val passengersAdapter = PassengersPrimaryAdapter(this@BookingDetailActivity)
                    adapter = passengersAdapter
                    if (bookingData.trip?.passengers.isNullOrEmpty()) {
                        tvPassengersNotAvailable.isVisible = true
                        rvPassengers.isVisible = false
                    } else {
                        tvPassengersNotAvailable.isVisible = false
                        rvPassengers.isVisible = true
                        passengersAdapter.submitList(bookingData.trip?.passengers)
                    }
                }
            }
        }
    }

    private fun setUpUserSideDesignationAndScheduleViews(bookingData: BookingDetailsData) {
        binding.apply {
            includeDestinationAndScheduleLayout.apply {
                includeTripRouteLayout.apply {
                    tvAddressFrom.text = checkStringValue(
                        this@BookingDetailActivity, bookingData.trip?.from_address
                    )
                    formatDateTime(
                        this@BookingDetailActivity, tvDepartureDate, bookingData.trip?.departureDate
                    )
                    tvAddressWhereto.text = checkStringValue(
                        this@BookingDetailActivity, bookingData.trip?.whereTo_address
                    )
                    tvPricePerPerson.text = checkPriceValue(bookingData.amount)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUserSideTripDetailsViews(bookingData: BookingDetailsData) {
        binding.includeUserSideTripDetails.apply {
            bookingData.trip?.luggageRestrictions?.let { it
                tvHCWeightValue.text = "${
                    checkLuggageRestrictionValue(it, LuggageType.HandCarry, this@BookingDetailActivity)
                }$WEIGHT_SIGN"

                tvSCWeightValue.text = "${
                    checkLuggageRestrictionValue(it , LuggageType.SuitCase, this@BookingDetailActivity)
                }$WEIGHT_SIGN"
            }

            tvRoundTripValue.text = if (bookingData.trip?.roundTrip == true) {
                getString(R.string.yes)
            } else {
                getString(R.string.no)
            }
            tvSmokingAllowedValue.text = if (bookingData.trip?.smokingPreference == true) {
                getString(R.string.yes)
            } else {
                getString(R.string.no)
            }
            bookingData.trip?.languagePreference.itOrNull({ tvLanguagePreferenceValue.text = it }, {
                tvLanguagePreferenceValue.text =
                    ContextCompat.getString(this@BookingDetailActivity, R.string.not_provided)
            })
            tvOtherRelevantDetailsValue.text = checkStringValue(
                this@BookingDetailActivity, bookingData.trip?.note
            )
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is Passenger) {
            startActivity(
                Intent(this@BookingDetailActivity,
                    ReviewsActivity::class.java).apply {
                    putExtra(USER_ID, data._id)
                }
            )
        }
    }


    private fun onUpdateStatusButtonClick(data: Booking, status: PickUpPassengersStatus) {
        val request = data.trip?.let { data._id?.let { it1 ->
            data.pickupStatus?._id?.let { it2 ->
                UpdatePickupStatusRequest(it,
                    it1, status.toString(), it2
                )
            }
        } }

        if (request != null) {
            updatePickUpPassengersStatus.updatePickupStatus(request = request)
            bindUpdatePickupStatusObserver()
        }
    }

    private fun bindUpdatePickupStatusObserver(){
        lifecycleScope.launch {
            updatePickUpPassengersStatus.updateTripStatusState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(
                            TAG, "bindUpdatePickupStatusObserver: Error -> " + it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        it.data.data?.let { tripsData ->
                            val booking = tripsData.bookings?.single { booking -> booking._id == bookingId }
                            if (booking != null) {
                                setUpViewForPassengerSidePickupStatus(booking)
                            }
                        }
                        Toast(this@BookingDetailActivity).showSuccessMessage(
                            this@BookingDetailActivity, getString(R.string.status_updated_successfully)
                        )
                    }
                    else -> {}
                }
            }

        }
    }
}