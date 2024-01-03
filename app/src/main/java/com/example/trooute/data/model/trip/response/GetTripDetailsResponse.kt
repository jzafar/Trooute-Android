package com.example.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetTripDetailsResponse(
    val `data`: TripsData? = null,
    val message: String? = null ?: "",
    val success: Boolean
): Parcelable