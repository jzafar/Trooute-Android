package com.example.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Driver(
    val _id: String? = null ?: "0",
    val carDetails: CarDetails? = null,
    val name: String? = null ?: "No name",
    val photo: String? = null ?: "",
    val isApprovedDriver: Boolean,
    val reviewsStats: ReviewsStats? = null
) : Parcelable