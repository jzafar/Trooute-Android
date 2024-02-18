package com.travel.trooute.data.model.review.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetReviewsResponse(
    val `data`: List<Reviews>? = null,
    val message: String? = null ?: "",
    val success: Boolean
): Parcelable
