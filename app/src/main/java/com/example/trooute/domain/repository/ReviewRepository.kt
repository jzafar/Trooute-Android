package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest

interface ReviewRepository {
    suspend fun createReview(request: CreateReviewRequest): Resource<BaseResponse>
}