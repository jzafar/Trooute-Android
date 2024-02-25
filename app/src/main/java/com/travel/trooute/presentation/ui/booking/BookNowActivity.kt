package com.travel.trooute.presentation.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.travel.trooute.R
import com.travel.trooute.core.util.GooglePlacesManager
import com.travel.trooute.core.util.Constants.CREATE_BOOKING_REQUEST
import com.travel.trooute.core.util.Constants.GET_TRIP_DETAIL
import com.travel.trooute.core.util.Constants.TRIP_ID
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.trip.response.TripsData
import com.travel.trooute.databinding.ActivityBookNowBinding
import com.travel.trooute.presentation.adapters.PassengersSecondaryAdapter
import com.travel.trooute.presentation.utils.Utils.formatDateTime
import com.travel.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.loadImage
import com.travel.trooute.presentation.utils.loadProfileImage
import com.travel.trooute.presentation.utils.setRVOverlayHorizontal
import com.travel.trooute.presentation.utils.showWarningMessage
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookNowActivity : AppCompatActivity() {

    private val TAG = "BookNowActivity"

    private lateinit var binding: ActivityBookNowBinding
    private lateinit var passengersAdapter: PassengersSecondaryAdapter
    private lateinit var googlePlacesManager: GooglePlacesManager

    private var tripID: String? = null
    private var personCount: Long = 1
    private var availableSeat: Long = 0
    private var totalAmount: Double = 0.0
    private var totalAmountResult: Double? = null
    private var placesPickUpLocationLatLng: LatLng? = null

    private var tripsData: TripsData? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_now)
        passengersAdapter = PassengersSecondaryAdapter()
        tripID = intent.getStringExtra(TRIP_ID).toString()
        tripsData = IntentCompat.getParcelableExtra(intent, GET_TRIP_DETAIL, TripsData::class.java)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getString(R.string.booking)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            includePickupLocationItemLayout.apply {
                googlePlacesManager = GooglePlacesManager(
                    this@BookNowActivity,
                    activityResultRegistry
                ) { placesAddress, placesLatLng ->
                    etPickUpLocation.setText(placesAddress)
                    placesPickUpLocationLatLng = placesLatLng
                }
                lifecycle.addObserver(googlePlacesManager)

                etPickUpLocation.setOnClickListener {
                    googlePlacesManager.launchGooglePlaces()
                }
            }

            setUpViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        binding.apply {
            tripsData?.let { tripsData ->
                includeTripsItemLayout.apply {
                    tvAvailableSeat.text = "${
                        checkLongValue(tripsData.availableSeats)
                    } " + getString(R.string.seats_available)

                    tripsData.availableSeats?.let { availableSeats ->
                        availableSeat = availableSeats
                    }

                    // Driver info
                    includeDriverInfo.apply {
                        loadProfileImage(imgUserProfile, tripsData.driver?.photo)
                        tvUserName.text = checkStringValue(
                            this@BookNowActivity,
                            tripsData.driver?.name
                        )
                        tvAvgRating.text = checkFloatValue(
                            tripsData.driver?.reviewsStats?.avgRating
                        )
                        tvTotalReviews.text = "(${
                            checkLongValue(
                                tripsData.driver?.reviewsStats?.totalReviews
                            )
                        })"
                        loadImage(imgCarImage, tripsData.driver?.carDetails?.photo)
                        tvCarModel.text = checkStringValue(
                            this@BookNowActivity,
                            tripsData.driver?.carDetails?.model
                        )
                        tvCarRegistrationNumber.text = checkStringValue(
                            this@BookNowActivity,
                            tripsData.driver?.carDetails?.registrationNumber
                        )
                    }

                    // Trip Details
                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            this@BookNowActivity,
                            tripsData.from_address
                        )
                        formatDateTime(
                            this@BookNowActivity,
                            tvDepartureDate,
                            tripsData.departureDate.toString()
                        )
                        tvAddressWhereto.text = checkStringValue(
                            this@BookNowActivity,
                            tripsData.whereTo_address
                        )
                    }

                    rvPassengersUserMode.apply {
                        this.setRVOverlayHorizontal()
                        adapter = passengersAdapter
                        passengersAdapter.submitList(tripsData.passengers)
                    }

                    tvPricePerPerson.text = checkPriceValue(
                        tripsData.pricePerPerson
                    )
                }

                // Multiply total amount with number of persons
                tripsData.pricePerPerson?.let {
                    totalAmount = it.times(personCount)
                }
                tvTotalAmount.text = checkPriceValue(totalAmount)
                // Update total amount result because it can't be null
                totalAmountResult = totalAmount

                tvClear.setOnClickListener {
                    includePassengersItemLayout.apply {
                        personCount = 1
                        tvPersonCount.text = personCount.toString()
                        tvTotalAmount.text = checkPriceValue(totalAmount)
                    }
                }

                includePassengersItemLayout.apply {
                    tvPersonCount.text = personCount.toString()
                    decrementPerson.setOnClickListener {
                        if (personCount > 1) {
                            personCount--
                            updateAmount(personCount)
                        }
                    }

                    incrementPerson.setOnClickListener {
                        if (personCount < availableSeat) {
                            personCount++
                            updateAmount(personCount)
                        } else {
                            Toast(this@BookNowActivity).showWarningMessage(
                                this@BookNowActivity,
                                getString(R.string.no_more_seats)
                            )
                        }
                    }
                }

                btnProceed.setOnClickListener {
                    if (
                        isFieldValid(
                            includePickupLocationItemLayout.etPickUpLocation,
                            getString(R.string.pickup_location)
                        )
                        && isFieldValid(
                            includePickupLocationItemLayout.etDestinationNote,
                            getString(R.string.other_relevent_details)
                        )
                    ) {
                        startActivity(
                            Intent(this@BookNowActivity, ConfirmBookingActivity::class.java).apply {
                                putExtra(GET_TRIP_DETAIL, tripsData)

                                val createBookingRequest = CreateBookingRequest(
                                    amount = totalAmountResult,
                                    note = includePickupLocationItemLayout.etDestinationNote.text.toString(),
                                    numberOfSeats = personCount,
                                    pickupLocation = CreateBookingRequest.PickupLocation(
                                        address = includePickupLocationItemLayout.etPickUpLocation.text.toString(),
                                        location = listOf(
                                            placesPickUpLocationLatLng?.latitude,
                                            placesPickUpLocationLatLng?.longitude
                                        )
                                    ),
                                    trip = tripID,
                                    plateFormFee = personCount.toDouble()
                                )
                                putExtra(CREATE_BOOKING_REQUEST, createBookingRequest)
                            }
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAmount(count: Long) {
        binding.apply {
            includePassengersItemLayout.apply {
                tvPersonCount.text = count.toString()
                totalAmountResult = count * totalAmount
                tvTotalAmount.text = checkPriceValue(totalAmountResult)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}