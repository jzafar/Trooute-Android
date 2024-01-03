package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL.REVIEW_END_POINT
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReviewAPI {
    @POST(REVIEW_END_POINT)
    suspend fun createReview(@Body body: CreateReviewRequest): Response<BaseResponse>
}