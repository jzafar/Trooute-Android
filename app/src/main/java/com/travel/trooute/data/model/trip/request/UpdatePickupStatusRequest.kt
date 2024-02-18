package com.travel.trooute.data.model.trip.request

data class UpdatePickupStatusRequest(
    val tripId: String,
    val bookingId: String,
    val pickupStatus: String,
    var pickupId: String,
)