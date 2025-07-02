package com.travel.trooute.data.model.trip.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.Driver
import com.travel.trooute.data.model.common.Location
import com.travel.trooute.data.model.common.Passenger
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripsData(
    val __v: Long? = null,
    val _id: String = null ?: "",
    var isAddedInWishList: Boolean,
    val availableSeats: Long? = null ?: 0,
    val departureDate: String? = null ?: "",
    val driver: Driver? = null,
    val from_address: String? = null ?: "",
    val from_location: Location? = null,
    val languagePreference: String? = null ?: "",
    val note: String? = null ?: "",
    val passengers: List<Passenger>? = null,
    val pricePerPerson: Double? = null,
    val smokingPreference: Boolean,
    val petPreference: Boolean,
    val status: String? = null ?: "",
    val totalAmount: Double? = null,
    val totalSeats: Long? = null,
    val whereTo_address: String? = null ?: "",
    val whereTo_location: Location? = null,
    val bookings: List<Booking>? = null,
    val trip: Trip? = null,
    val luggageRestrictions: List<LuggageRestrictions?>,
    val roundTrip: Boolean,
    val paymentTypes: List<String> = listOf("cash")
) : Parcelable