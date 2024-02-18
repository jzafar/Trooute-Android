package com.travel.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewsStats(
    val avgRating: Float? = null ?: 0.0F,
    val ratings: Ratings? = null,
    val totalReviews: Long? = 0
) : Parcelable