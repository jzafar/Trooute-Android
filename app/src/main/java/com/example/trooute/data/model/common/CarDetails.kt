package com.example.trooute.data.model.common

import android.os.Parcelable
import com.example.trooute.data.model.common.ReviewsStats
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarDetails(
    val color: String? = null ?: "",
    val driverLicense: String? = null ?: "",
    val make: String? = null ?: "",
    val model: String? = null ?: "",
    val photo: String? = null ?: "",
    val registrationNumber: String? = null ?: "",
    val reviews: List<String>? = null,
    val reviewsStats: ReviewsStats? = null,
    val year: Long? = null
): Parcelable