package com.example.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val _id: String? = null ?: "0",
    val name: String? = null ?: "No name",
    val phoneNumber: String? = null ?: "No phone number",
    val photo: String? = null ?: "",
    val reviewsStats: ReviewsStats? = null
) : Parcelable