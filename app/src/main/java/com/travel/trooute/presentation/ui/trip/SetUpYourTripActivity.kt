package com.travel.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.GooglePlacesManager
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.request.CreateTripRequest
import com.travel.trooute.data.model.trip.response.LuggageRestrictions
import com.travel.trooute.databinding.ActivitySetUpYourTripBinding
import com.travel.trooute.presentation.utils.DateAndTimeManager
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isFieldPriceValid
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.priceToDouble
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.tripviewmodel.CreateTripViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.internal.ViewUtils
import com.travel.trooute.data.model.trip.response.LuggageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SetUpYourTripActivity : AppCompatActivity() {

    private val TAG = "SetUpYourTrip"
    private var totalSeats: Long = 1
    private var departureDate: String = ""
    private var departureTime: String = ""
    private var roundTrip = false
    private var smokingPreference: Boolean = false
    private var petsPreference: Boolean = false
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
                this.toolbarTitle.text = getString(R.string.set_up_trip)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            includeDestinationAndSchedule.apply {
                calendarView.minDate = Date().time
//                calendarView.layout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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
                        incrementSeats.setEnabled(true);
                        totalSeats--
                        tvTotalSeats.text = "$totalSeats"
                    }
                    if (totalSeats.toInt() == 1) {
                        decrementSeats.setEnabled(false);
                    }
                }

                incrementSeats.setOnClickListener {
                    if (totalSeats < Constants.MAX_PASSENGERS){
                        decrementSeats.setEnabled(true);
                        totalSeats++
                        tvTotalSeats.text = "$totalSeats"
                    }else {
                        Toast(this@SetUpYourTripActivity).showErrorMessage(
                            this@SetUpYourTripActivity,
                            resources.getString(R.string.max_passengers_allowed, Constants.MAX_PASSENGERS)

                        )
                    }
                    if(totalSeats.toInt() == Constants.MAX_PASSENGERS) {
                        incrementSeats.setEnabled(false);
                    }

                }

                val currencyTextWatcher = object : TextWatcher {
                    override fun afterTextChanged(editable: Editable?) {
                        when {
                            editable.isNullOrEmpty() -> return
                            Regex("\\$\\d+").matches(editable.toString()) -> return
                            editable.toString() == "€ " -> editable.clear()
                            editable.startsWith("€ ").not() -> editable.insert(0, "€ ")
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                }

                etPrice.addTextChangedListener(currencyTextWatcher)

                switchSmokingPreference.setOnCheckedChangeListener { buttonView, isChecked ->
                    smokingPreference = isChecked
                }

                switchPetsPreference.setOnCheckedChangeListener { buttonView, isChecked ->
                    petsPreference = isChecked
                }
                switchLanguagePreference.setOnCheckedChangeListener{ buttonView, isChecked ->
                    if (isChecked) {
                        tlvLanguagePreference.isEnabled = true
                    } else {
                        tlvLanguagePreference.isEnabled = false
                        tlvLanguagePreference.setText("")
                    }
                }

                btnPostTrip.setOnClickListener {
                    Log.e(TAG, "onCreate: departureDate -> $departureDate")
                    Log.e(TAG, "onCreate: departureTime -> $departureTime")
                    val luggageList: MutableList<LuggageRestrictions> = arrayListOf()
                    val hcWeight =  includeTripDetailsDriverItemLayout.tvHandCarryRestrictionWeight.text.toString()
                    val handCarry = LuggageRestrictions(LuggageType.HandCarry, hcWeight.toLongOrNull())
                    luggageList.add(handCarry)
                    val suitcase = LuggageRestrictions(LuggageType.SuitCase, includeTripDetailsDriverItemLayout.tvLanguageRestrictionWeightSuitcase.text.toString().toLongOrNull())
                    luggageList.add(suitcase)

                    if (
                        isFieldValid(
                            includeDestinationAndSchedule.etStartingLocation,
                            getString(R.string.start_locaction)
                        )
                        && isFieldValid(
                            includeDestinationAndSchedule.etDestinationLocation,
                            getString(R.string.destination_location)
                        )
                        && isFieldPriceValid(
                            includeTripDetailsDriverItemLayout.etPrice,
                            getString(R.string.price)
                        )
                    ) {
                        Log.d(TAG, "onCreate: departureDate -> $departureDate")
                        Log.d(TAG, "onCreate: departureTime -> $departureTime")
                        var languagePreference: String? = null
                        if (binding.includeTripDetailsDriverItemLayout.switchLanguagePreference.isChecked) {
                            var language = binding.includeTripDetailsDriverItemLayout.tlvLanguagePreference.text
                            languagePreference = language.toString()
                        }
                        val price =  etPrice.text.toString()
                        val number: Double = priceToDouble(price)
                        createTripViewModel.createTrip(
                            CreateTripRequest(
                                departureDate = "$departureDate, $departureTime",
                                from_address = includeDestinationAndSchedule.etStartingLocation.text.toString(),
                                from_location = listOf(
                                    placesStartLocationLatLng?.longitude,
                                    placesStartLocationLatLng?.latitude
                                ),

                                languagePreference = languagePreference,
                                luggageRestrictions = luggageList,
                                note = etNote.text.toString(),
                                pricePerPerson = number,
                                smokingPreference = smokingPreference,
                                petsPreference = petsPreference,
                                roundTrip = roundTrip,
                                status = "",
                                totalSeats = totalSeats,
                                whereTo_address = includeDestinationAndSchedule.etDestinationLocation.text.toString(),
                                whereTo_location = listOf(
                                    placesDestinationLocationLatLng?.longitude,
                                    placesDestinationLocationLatLng?.latitude
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

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}