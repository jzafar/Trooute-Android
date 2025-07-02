package com.travel.trooute.data.model.auth.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.CarDetails
import com.travel.trooute.data.model.common.ReviewsStats
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val __v: Int? = null,
    val _id: String? = null,
    val createdAt: String? = null,
    val driverMode: Boolean = false,
    val email: String? = null,
    val emailverifyOTP: String? = null,
    var isApprovedDriver: String? = null,
    var isEmailVerified: Boolean = false,
    var name: String? = null,
    var role: String? = null,
    var phoneNumber: String? = null,
    var photo: String? = null,
    val updatedAt: String? = null,
    var reviewsStats: ReviewsStats? = null,
    var carDetails: CarDetails? = null,
    var gender: String? = null,
    var stripeConnectedAccountId: String? = null,
    var payPalEmail: String? = null
) : Parcelable