package com.travel.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickupLocation(
    val address: String? = null ?: "No address",
    val location: Location? = null
) : Parcelable