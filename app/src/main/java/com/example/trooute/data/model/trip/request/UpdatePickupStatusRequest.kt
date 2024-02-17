package com.example.trooute.data.model.trip.request

import com.example.trooute.data.model.trip.response.LanguageRestriction

data class UpdatePickupStatusRequest(
    val tripId: String,
    val bookingId: String,
    val pickupStatus: String,
    var pickupId: String,
)