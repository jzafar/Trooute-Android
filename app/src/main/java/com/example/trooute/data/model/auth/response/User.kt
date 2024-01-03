package com.example.trooute.data.model.auth.response

import android.os.Parcelable
import com.example.trooute.data.model.common.CarDetails
import com.example.trooute.data.model.common.ReviewsStats
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val __v: Int? = null,
    val _id: String? = null,
    val createdAt: String? = null,
    val driverMode: Boolean = false,
    val email: String? = null,
    val emailverifyOTP: String? = null,
    val isApprovedDriver: String? = null,
    val isEmailVerified: Boolean = false,
    val name: String? = null,
    val role: String? = null,
    val phoneNumber: String? = null,
    val photo: String? = null,
    val updatedAt: String? = null,
    val reviewsStats: ReviewsStats? = null,
    val carDetails: CarDetails? = null
) : Parcelable