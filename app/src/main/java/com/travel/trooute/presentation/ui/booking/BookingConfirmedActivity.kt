package com.travel.trooute.presentation.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.travel.trooute.R
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.trip.response.TripsData
import com.travel.trooute.databinding.ActivityBookingConfirmedBinding
import com.travel.trooute.presentation.ui.main.MainActivity
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.CREATE_BOOKING_REQUEST
import com.travel.trooute.core.util.Constants.PLATFORM_FEE_PRICE
import com.travel.trooute.core.util.Constants.PRICE_SIGN
import com.travel.trooute.presentation.utils.StatusChecker.checkStatus
import com.travel.trooute.presentation.utils.Utils.formatDateTime
import com.travel.trooute.presentation.utils.Utils.getSubString
import com.travel.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.travel.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.loadImage
import com.travel.trooute.presentation.utils.loadProfileImage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookingConfirmedActivity : AppCompatActivity() {

    private val TAG = "BookingConfirmed"

    private lateinit var binding: ActivityBookingConfirmedBinding

    private val tripsData: TripsData? by lazy {
        IntentCompat.getParcelableExtra(intent, Constants.GET_TRIP_DETAIL, TripsData::class.java)
    }

    private val createBookingRequest: CreateBookingRequest? by lazy {
        IntentCompat.getParcelableExtra(
            intent, CREATE_BOOKING_REQUEST, CreateBookingRequest::class.java
        )
    }

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_confirmed)

        binding.apply {
            includeBookingDetailLayout.apply {
                ltStatusSection.isVisible = true
                ltSeatsSection.isVisible = false
                tvStatusDesc.isVisible = false

                tripsData.let { trips ->
//                    checkStatus(sharedPreferenceManager.isDriverApproved(), tvStatus, trips.status)
                    checkStatus(
                        sharedPreferenceManager.driverMode(),
                        tvStatus,
                        ContextCompat.getString(this@BookingConfirmedActivity, R.string.waiting)
                    )
                    tvBookingId.text = "Booking # ${getSubString(trips?._id)}"
                    formatDateTime(
                        this@BookingConfirmedActivity,
                        tvDepartureDate,
                        trips?.departureDate.toString()
                    )
                    includeUserInfo.apply {
                        // User info
                        loadProfileImage(imgUserProfile, trips?.driver?.photo)
                        tvUserName.text = checkStringValue(
                            this@BookingConfirmedActivity, trips?.driver?.name
                        )
                        tvAvgRating.text = checkFloatValue(trips?.driver?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(trips?.driver?.reviewsStats?.totalReviews)
                        })"

                        // Car info
                        loadImage(imgCarImage, trips?.driver?.carDetails?.photo.toString())
                        tvCarModel.text = checkStringValue(
                            this@BookingConfirmedActivity,
                            trips?.driver?.carDetails?.model
                        )
                        tvCarRegistrationNumber.text = checkStringValue(
                            this@BookingConfirmedActivity,
                            trips?.driver?.carDetails?.registrationNumber
                        )
                    }

                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            this@BookingConfirmedActivity, trips?.from_address
                        )
                        formatDateTime(
                            this@BookingConfirmedActivity,
                            tvDepartureDate,
                            trips?.departureDate.toString()
                        )
                        tvAddressWhereto.text = checkStringValue(
                            this@BookingConfirmedActivity, trips?.whereTo_address
                        )
                    }

                    val platFormFee = PLATFORM_FEE_PRICE * createBookingRequest?.numberOfSeats!!

                    tvNxSeats.text = checkNumOfSeatsValue(createBookingRequest?.numberOfSeats)
                    tvNxSeatsPrice.text = checkPriceValue(createBookingRequest?.amount)
                    tvPlatformFeePrice.text = "$PRICE_SIGN$platFormFee"
                    tvTotalPrice.text = checkPriceValue(
                        createBookingRequest?.amount?.plus(platFormFee)
                    )
                }
            }

            // Move to MainActivity without refreshing or recreating MainActivity
            val intent = Intent(
                this@BookingConfirmedActivity, MainActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("trip_booked", true)

            btnBackToHome.setOnClickListener {
                startActivity(intent)
            }
        }
    }
}