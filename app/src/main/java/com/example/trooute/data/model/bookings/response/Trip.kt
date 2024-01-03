package com.example.trooute.data.model.bookings.response

import android.os.Parcelable
import com.example.trooute.data.model.common.Driver
import com.example.trooute.data.model.common.Passenger
import com.example.trooute.data.model.trip.response.LanguageRestriction
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val _id: String? = null ?: "0",
    val departureDate: String? = null ?: "No departure date",
    val driver: Driver? = null,
    val passengers: List<Passenger>? = null,
    val from_address: String? = null ?: "No address",
    val whereTo_address: String? = null ?: "No address",
    val luggageRestrictions: LanguageRestriction? = null,
    val roundTrip: Boolean,
    val smokingPreference: Boolean,
    val languagePreference: String? = null?:"Not provided",
    val note: String? = null?:"Not provided"
) : Parcelable