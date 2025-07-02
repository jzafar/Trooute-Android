package com.travel.trooute.data.model.bookings.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.Driver
import com.travel.trooute.data.model.common.Passenger
import com.travel.trooute.data.model.trip.response.LuggageRestrictions
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val _id: String? = null ?: "0",
    val departureDate: String? = null ?: "No departure date",
    val driver: Driver? = null,
    val passengers: List<Passenger>? = null,
    val from_address: String? = null ?: "No address",
    val whereTo_address: String? = null ?: "No address",
    val pricePerPerson: Double? = null ?: 0.0,
    val luggageRestrictions: List<LuggageRestrictions?>,
    val roundTrip: Boolean,
    val smokingPreference: Boolean,
    val languagePreference: String? = null?:"Not provided",
    val note: String? = null?:"Not provided",
    val availableSeats: Double? = null,
    val status: String = "Scheduled",
    val paymentTypes: List<String>? = null
) : Parcelable