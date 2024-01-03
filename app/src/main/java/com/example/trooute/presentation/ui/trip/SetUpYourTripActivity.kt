package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.trooute.R
import com.example.trooute.core.util.GooglePlacesManager
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.data.model.trip.response.LanguageRestriction
import com.example.trooute.databinding.ActivitySetUpYourTripBinding
import com.example.trooute.presentation.utils.DateAndTimeManager
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.tripviewmodel.CreateTripViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SetUpYourTripActivity : AppCompatActivity() {

    private val TAG = "SetUpYourTrip"
    private var totalSeats: Long = 1
    private var departureDate: String = ""
    private var departureTime: String = ""
    private var roundTrip = false
    private var smokingPreference: Boolean = false
    private var languagePreference: String? = null
    private val languageArrayList = ArrayList<String>()

    private var placesStartLocationLatLng: LatLng? = null
    private var placesStartLocationAddress: String? = null
    private var placesDestinationLocationLatLng: LatLng? = null
    private var placesDestinationLocationAddress: String? = null
    private var isStartLocationRequired = false

    private lateinit var binding: ActivitySetUpYourTripBinding
    private lateinit var dateAndTimeManager: DateAndTimeManager
    private lateinit var googlePlacesManager: GooglePlacesManager

    private val createTripViewModel: CreateTripViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_up_your_trip)
        dateAndTimeManager = DateAndTimeManager(this)

        googlePlacesManager = GooglePlacesManager(
            this,
            activityResultRegistry
        ) { placesAddress, placesLatLng ->
            binding.includeDestinationAndSchedule.apply {
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
            includeAppBar.apply {
                this.toolbarTitle.text = "Set Up Your Trip"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            includeDestinationAndSchedule.apply {
                etStartingLocation.setOnClickListener {
                    isStartLocationRequired = true
                    googlePlacesManager.launchGooglePlaces()
                }

                etDestinationLocation.setOnClickListener {
                    isStartLocationRequired = false
                    googlePlacesManager.launchGooglePlaces()
                }

                dateAndTimeManager.initializeCalendar(calendarView) {
                    departureDate = it
                    Log.e(TAG, "onCreate: departure date -> $it")
                }

                dateAndTimeManager.initializeTimePicker(tvTime, etTime) {
                    departureTime = it
                    Log.e(TAG, "onCreate: departure time -> $it")
                }
            }

            includeTripDetailsDriverItemLayout.apply {
                decrementSeats.setOnClickListener {
                    if (totalSeats > 1) {
                        totalSeats--
                        tvTotalSeats.text = "$totalSeats"
                    }
                }

                incrementSeats.setOnClickListener {
                    totalSeats++
                    tvTotalSeats.text = "$totalSeats"
                }

                switchRoundTrip.setOnCheckedChangeListener { buttonView, isChecked ->
                    roundTrip = isChecked
                }

                switchSmokingPreference.setOnCheckedChangeListener { buttonView, isChecked ->
                    smokingPreference = isChecked
                }

                setUpLanguageLanguage(actLanguage)

                btnPostTrip.setOnClickListener {
                    Log.e(TAG, "onCreate: departureDate -> $departureDate")
                    Log.e(TAG, "onCreate: departureTime -> $departureTime")
                    if (
                        isFieldValid(
                            includeDestinationAndSchedule.etStartingLocation,
                            "Start location"
                        )
                        && isFieldValid(
                            includeDestinationAndSchedule.etDestinationLocation,
                            "Destination location"
                        )
                        && isFieldValid(
                            includeTripDetailsDriverItemLayout.etPrice,
                            "Price"
                        )
                        && isFieldValid(
                            includeTripDetailsDriverItemLayout.tvLanguageRestrictionType,
                            "Type"
                        )
                        && isFieldValid(
                            includeTripDetailsDriverItemLayout.tvLanguageRestrictionWeight,
                            "weight"
                        )
                    ) {
                        Log.e(TAG, "onCreate: departureDate -> $departureDate")
                        Log.e(TAG, "onCreate: departureTime -> $departureTime")
                        createTripViewModel.createTrip(
                            CreateTripRequest(
                                departureDate = "$departureDate, $departureTime",
                                from_address = includeDestinationAndSchedule.etStartingLocation.text.toString(),
                                from_location = listOf(
                                    placesStartLocationLatLng?.latitude,
                                    placesStartLocationLatLng?.longitude
                                ),
                                languagePreference = languagePreference,
                                luggageRestrictions = LanguageRestriction(
                                    text = tvLanguageRestrictionType.text.toString(),
                                    weight = tvLanguageRestrictionWeight.text.toString().toLong()
                                ),
                                note = etNote.text.toString(),
                                pricePerPerson = etPrice.text.toString().toDouble(),
                                smokingPreference = smokingPreference,
                                roundTrip = roundTrip,
                                status = "",
                                totalSeats = totalSeats,
                                whereTo_address = includeDestinationAndSchedule.etDestinationLocation.text.toString(),
                                whereTo_location = listOf(
                                    placesDestinationLocationLatLng?.latitude,
                                    placesDestinationLocationLatLng?.longitude
                                )
                            )
                        )

                        bindCreateTripObserver()
                    }
                }
            }
        }
    }

    private fun bindCreateTripObserver() {
        lifecycleScope.launch {
            createTripViewModel.createTripState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@SetUpYourTripActivity).showErrorMessage(
                            this@SetUpYourTripActivity,
                            it.message.toString()
                        )

                        Log.e(TAG, "bindCreateTripObserver: Error -> " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@SetUpYourTripActivity).showSuccessMessage(
                            this@SetUpYourTripActivity,
                            it.data.message.toString()
                        )

                        finish()

                        Log.e(
                            TAG,
                            "bindCreateTripObserver: Success -> " + it.data.message.toString()
                        )
                    }
                }
            }
        }
    }

    private fun setUpLanguageLanguage(
        actLanguagePreference: AutoCompleteTextView
    ) {
        languageArrayList.add("Arabic")
        languageArrayList.add("Urdu")
        languageArrayList.add("English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languageArrayList)

        binding.includeTripDetailsDriverItemLayout.switchLanguagePreference.setOnCheckedChangeListener { buttonView, isChecked ->
            actLanguagePreference.apply {
                if (isChecked) {
                    setAdapter(adapter) // Set the adapter (assuming 'adapter' is your ArrayAdapter)
                    isFocusable = true // Enable focus
                    isFocusableInTouchMode = true // Enable touch interaction

                    setOnItemClickListener { parent, view, position, id ->
                        languagePreference = parent.getItemAtPosition(position).toString()
                    }
                } else {
                    setAdapter(null) // Remove the adapter
                    isFocusable = false // Disable focus
                    isFocusableInTouchMode = false // Disable touch interaction
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}