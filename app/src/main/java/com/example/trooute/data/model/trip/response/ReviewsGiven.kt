package com.example.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewsGiven(
    val __v: Long? = null,
    val _id: String? = null ?: "",
    var comment: String? = null ?: "",
    var rating: Float? = null ?: 0.0F,
    val target: String? = null ?: "",
    val targetType: String? = null ?: "",
    val trip: String? = null ?: "",
    val user: String? = null ?: ""
) : Parcelable