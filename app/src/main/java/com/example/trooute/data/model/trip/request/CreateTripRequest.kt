package com.example.trooute.data.model.trip.request

import com.example.trooute.data.model.trip.response.LanguageRestriction

data class CreateTripRequest(
    val departureDate: String?,
    val from_address: String?,
    val from_location: List<Double?>,
    val languagePreference: String?,
    val luggageRestrictions: LanguageRestriction,
    val note: String?,
    val pricePerPerson: Double?,
    val smokingPreference: Boolean,
    val roundTrip: Boolean,
    val status: String?,
    val totalSeats: Long?,
    val whereTo_address: String?,
    val whereTo_location: List<Double?>
)