package com.travel.trooute.data.model.trip.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.Driver
import com.travel.trooute.data.model.common.Location
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val __v: Long? = null,
    val _id: String? = null ?: "",
    val availableSeats: Long? = null,
    val departureDate: String? = null ?: "",
    val driver: Driver? = null,
    val from_address: String? = null ?: "",
    val from_location: Location? = null,
    val languagePreference: String? = null ?: "",
    val note: String? = null ?: "",
    val pricePerPerson: Double? = null ?: 0.0,
    val smokingPreference: Boolean,
    val status: String? = null ?: "",
    val totalAmount: Double? = null ?: 0.0,
    val totalSeats: Long? = null ?: 0,
    val whereTo_address: String? = null ?: "",
    val whereTo_location: Location? = null,
    val luggageRestrictions: LanguageRestriction? = null,
    val roundTrip: Boolean
) : Parcelable