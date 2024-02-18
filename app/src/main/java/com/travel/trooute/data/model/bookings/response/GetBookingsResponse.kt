package com.travel.trooute.data.model.bookings.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetBookingsResponse(
    val `data`: List<BookingData>? = null,
    val message: String? = null ?: "",
    val success: Boolean
):Parcelable