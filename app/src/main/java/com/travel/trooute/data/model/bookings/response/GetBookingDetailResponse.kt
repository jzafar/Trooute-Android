package com.travel.trooute.data.model.bookings.response

data class GetBookingDetailResponse(
    val `data`: BookingDetailsData? = null,
    val message: String? = null ?: "",
    val success: Boolean
)