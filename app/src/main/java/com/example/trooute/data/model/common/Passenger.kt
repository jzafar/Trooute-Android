package com.example.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passenger(
    val _id: String? = null ?: "0",
    val photo: String? = null ?: ""
): Parcelable