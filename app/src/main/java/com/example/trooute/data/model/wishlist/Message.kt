package com.example.trooute.data.model.wishlist

import com.example.trooute.data.model.common.Passenger

data class Message(
    val __v: Int,
    val _id: String?,
    val availableSeats: Long?,
    val departureDate: String?,
    val driver: String?,
    val from_address: String?,
    val from_location: FromLocation?,
    val languagePreference: String?,
    val luggageRestrictions: LuggageRestrictions?,
    val note: String?,
    val passengers: List<Passenger>?,
    val pricePerPerson: Double?,
    val roundTrip: Boolean,
    val smokingPreference: Boolean,
    val status: String?,
    val totalAmount: Double?,
    val totalSeats: Long?,
    val whereTo_address: String?,
    val whereTo_location: WhereToLocation?
)