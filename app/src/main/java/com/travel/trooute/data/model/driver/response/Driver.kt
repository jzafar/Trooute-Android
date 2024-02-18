package com.travel.trooute.data.model.driver.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.CarDetails
import kotlinx.parcelize.Parcelize

@Parcelize
data class Driver(
    val _id: String? = null ?: "",
    val carDetails: CarDetails? = null,
    val driverMode: Boolean,
    val email: String? = null ?: "",
    val isApprovedDriver: Boolean,
    val name: String? = null ?: ""
) : Parcelable