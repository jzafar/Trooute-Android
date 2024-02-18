package com.travel.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetTripsResponse(
    val `data`: List<TripsData>? = null,
    val message: String? = null ?: "",
    val success: Boolean
): Parcelable