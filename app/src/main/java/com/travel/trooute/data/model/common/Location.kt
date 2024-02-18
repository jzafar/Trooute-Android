package com.travel.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val coordinates: List<Double>? = null,
    val type: String? = null ?: "No type"
) : Parcelable