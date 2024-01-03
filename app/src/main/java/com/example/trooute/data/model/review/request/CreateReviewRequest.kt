package com.example.trooute.data.model.review.request

data class CreateReviewRequest(
    val comment: String? = null,
    val rating: Float? = null,
    val targetId: String? = null,
    val targetType: String? = null,
    val trip: String? = null
)