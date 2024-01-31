package com.example.trooute.presentation.ui.booking

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants.ACCEPT_BOOKING_BODY
import com.example.trooute.core.util.Constants.ACCEPT_BOOKING_TITLE
import com.example.trooute.core.util.Constants.BOOKED_CANCELLED_BODY
import com.example.trooute.core.util.Constants.BOOKED_CANCELLED_TITLE
import com.example.trooute.core.util.Constants.BOOKING_ID
import com.example.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.example.trooute.core.util.Constants.MUTABLE_CONTENT
import com.example.trooute.core.util.Constants.PLATFORM_FEE_PRICE
import com.example.trooute.core.util.Constants.PRICE_SIGN
import com.example.trooute.core.util.Constants.TONE
import com.example.trooute.core.util.Constants.TOPIC
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Constants.WEIGHT_SIGN
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.bookings.response.BookingDetailsData
import com.example.trooute.data.model.chat.Users
import com.example.trooute.data.model.notification.NotificationRequest
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.databinding.ActivityBookingDetailBinding
import com.example.trooute.presentation.adapters.PassengersPrimaryAdapter
import com.example.trooute.presentation.ui.chat.MessageActivity
import com.example.trooute.presentation.ui.main.MakePaymentActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.ValueChecker.itOrNull
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVHorizontal
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.bookingviewmodel.GetBookingDetailsViewModel
import com.example.trooute.presentation.viewmodel.bookingviewmodel.ProcessBookingViewModel
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.example.trooute.presentation.viewmodel.reviewviewmodel.CreateReviewViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BookingDetailActivity : AppCompatActivity() {

    private val TAG = "BookingDetail"

    private lateinit var binding: ActivityBookingDetailBinding
    private lateinit var bookingId: String
    private lateinit var skeleton: Skeleton

    private var authModelInfo: User? = null

    private val getBookingDetailsViewModel: GetBookingDetailsViewModel by viewModels()
    private val processBookingViewModel: ProcessBookingViewModel by viewModels()
    private val createReviewViewModel: CreateReviewViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

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
                this.toolbarTitle.text = "Booking Detail"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = ltRoot.createSkeleton()
            skeleton.showSkeleton()

            getBookingDetailsViewModel.getBookingDetails(bookingId = bookingId)
            bindGetBookingDetailsObserver()
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
                            Log.e(TAG, "bindGetBookingDetailsObserver: Success -> " + it.data)
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
                                        ltDriverSidePickupLocation.isVisible = false
                                    }
                                    setUpUserSideReviewViews(bookingData)
                                    setUpUserSideDriverDetailsViews(bookingData)
                                    setUpUserSidePassengersViews(bookingData)
                                    setUpUserSideDesignationAndScheduleViews(bookingData)
                                    setUpUserSideTripDetailsViews(bookingData)
                                }
                            }

                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpBookingDetailViews(bookingData: BookingDetailsData) {
        binding.apply {
            when (bookingData.status) {
                getString(R.string.waiting) -> {
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
                        tvBookingId.text = "Booking # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

//                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
//                        tvNxSeatsPrice.text = checkPriceValue(bookingData.amount)

                        if (sharedPreferenceManager.driverMode()) {
                            ltPlatformFee.isVisible = false
                            ltCancelUserSide.isVisible = false
                            ltCancelAccept.isVisible = true

                            tvTotalPrice.text = checkPriceValue(
                                bookingData.amount
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

                getString(R.string.canceled) -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = true
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = false

                    includeCancelledLayout.apply {
                        tvStatus.text = ContextCompat.getString(
                            this@BookingDetailActivity, R.string.cancelled
                        )
                        tvBookingId.text = "Booking # ${getSubString(bookingData._id)}"
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

                        if (sharedPreferenceManager.driverMode()) {
                            ltPlatformFee.isVisible = false
                            tvTotalPrice.text = checkPriceValue(bookingData.amount)
                        } else {
                            ltPlatformFee.isVisible = true
                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                            tvTotalPrice.text = checkPriceValue(
                                pricePerSeat + platFormFee
                            )
                        }
                    }
                }

                getString(R.string.approved) -> {
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
                        tvBookingId.text = "Booking # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

//                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
//                        tvNxSeatsPrice.text = checkPriceValue(bookingData.amount)

//                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!

                        if (sharedPreferenceManager.driverMode()) {
                            ltDriverSideButton.isVisible = true
                            ltUserSideButtons.isVisible = false
                            ltPlatformFee.isVisible = false

                            tvTotalPrice.text = checkPriceValue(bookingData.amount)

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
                            ltPlatformFee.isVisible = true



                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"

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

                getString(R.string.confirmed) -> {
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
                        tvBookingId.text = "Booking # ${getSubString(bookingData._id)}"
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
                            ltPlatformFee.isVisible = false
                            tvTotalPrice.text = checkPriceValue(bookingData.amount)

                            btnCancelBooking.setOnClickListener {
                                processBookingViewModel.cancelBooking(bookingId = bookingData._id.toString())
                                bindProcessBookingObserver(
                                    BOOKED_CANCELLED_TITLE,
                                    BOOKED_CANCELLED_BODY,
                                    bookingData.user?._id.toString()
                                )
                            }
                        } else {
                            ltPlatformFee.isVisible = true
                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                            tvTotalPrice.text = checkPriceValue(
                                bookingData.amount
                            )

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

                getString(R.string.completed) -> {
                    includeWaitingLayout.mcWaitingBooking.isVisible = false
                    includeCancelledLayout.mcCancelledBooking.isVisible = false
                    includeApprovedLayout.mcApprovedBooking.isVisible = false
                    includeConfirmedLayout.mcConfirmedBooking.isVisible = false
                    includeCompletedLayout.mcCompletedBooking.isVisible = true

                    includeCompletedLayout.apply {
                        tvStatus.text = checkStringValue(
                            this@BookingDetailActivity, bookingData.status
                        )
                        tvBookingId.text = "Booking # ${getSubString(bookingData._id)}"
                        formatDateTime(
                            this@BookingDetailActivity,
                            tvDepartureDate,
                            bookingData.trip?.departureDate.toString()
                        )

                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!
                        val pricePerSeat = (bookingData.trip?.pricePerPerson?.toDouble() ?: 0.0) * bookingData.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)

//                        tvNxSeats.text = checkNumOfSeatsValue(bookingData.numberOfSeats)
//                        tvNxSeatsPrice.text = checkPriceValue(bookingData.amount)

//                        val platFormFee = PLATFORM_FEE_PRICE * bookingData.numberOfSeats!!

                        if (sharedPreferenceManager.driverMode()) {
                            ltPlatformFee.isVisible = false
                            tvTotalPrice.text = checkPriceValue(bookingData.amount)
                        } else {
                            ltPlatformFee.isVisible = true
                            tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                            tvTotalPrice.text = checkPriceValue(
                                platFormFee + pricePerSeat
                            )
                        }
                    }
                }
            }
        }
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
                tvAvgRating.text = checkFloatValue(bookingData.user?.reviewsStats?.avgRating)
                tvTotalReviews.text = "(${
                    checkLongValue(
                        bookingData.user?.reviewsStats?.totalReviews
                    )
                })"

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
                tvAvgRating.text = checkFloatValue(
                    bookingData.trip?.driver?.reviewsStats?.avgRating
                )
                tvTotalReviews.text = "(${
                    checkLongValue(bookingData.trip?.driver?.reviewsStats?.totalReviews)
                })"
//                ltCallInboxSection.isVisible = true


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
                    this@BookingDetailActivity, bookingData.trip?.driver?.carDetails?.model
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
            } Seats Available"
            includePassengersInfoLayout.apply {
                rvPassengers.apply {
                    setRVHorizontal()
                    val passengersAdapter = PassengersPrimaryAdapter()
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
            tvTypeValue.text = checkStringValue(
                this@BookingDetailActivity, bookingData.trip?.luggageRestrictions?.text
            )
            tvWeightValue.text = "${
                checkLongValue(bookingData.trip?.luggageRestrictions?.weight)
            }$WEIGHT_SIGN"
            tvRoundTripValue.text = if (bookingData.trip?.roundTrip == true) {
                "Yes"
            } else {
                "No"
            }
            tvSmokingAllowedValue.text = if (bookingData.trip?.smokingPreference == true) {
                "Yes"
            } else {
                "No"
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
}