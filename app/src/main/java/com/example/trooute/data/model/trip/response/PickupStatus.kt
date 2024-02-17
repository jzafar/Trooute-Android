package com.example.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickupStatus(
    val __v: Long? = null,
    val _id: String? = null ?: "",
    var driverStatus: String? = null ?: "",
    var passengerStatus: String? = null ?: "",
    val booking: String? = null ?: "",
    val trip: String? = null ?: ""
) : Parcelable