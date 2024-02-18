package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.review.request.CreateReviewRequest
import com.travel.trooute.data.model.review.response.GetReviewsResponse

interface ReviewRepository {
    suspend fun createReview(request: CreateReviewRequest): Resource<BaseResponse>
    suspend fun getReviews(userId: String): Resource<GetReviewsResponse>
}