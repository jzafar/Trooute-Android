package com.travel.trooute.data.model.bookings.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverToUserReview(
    val __v: Long? = null ?: 0,
    val _id: String? = null ?: "0",
    val comment: String? = null ?: "No comment",
    val rating: Double? = null ?: 0.0,
    val target: String? = null ?: "No target",
    val targetType: String? = null ?: "No target type",
    val trip: String? = null ?: "No trip",
    val user: String? = null ?: "No user"
) : Parcelable