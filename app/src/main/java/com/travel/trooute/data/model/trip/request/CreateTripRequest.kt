package com.travel.trooute.data.model.trip.request

import com.travel.trooute.data.model.trip.response.LuggageRestrictions

data class CreateTripRequest(
    val departureDate: String?,
    val from_address: String?,
    val from_location: List<Double?>,
    val languagePreference: String?,
    val luggageRestrictions: List<LuggageRestrictions?>,
    val note: String?,
    val pricePerPerson: Double?,
    val smokingPreference: Boolean,
    val petsPreference: Boolean,
    val roundTrip: Boolean,
    val status: String?,
    val totalSeats: Long?,
    val whereTo_address: String?,
    val whereTo_location: List<Double?>
)