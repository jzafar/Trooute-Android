package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL
import com.example.trooute.core.util.URL.REVIEW_END_POINT
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.data.model.review.response.GetReviewsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewAPI {
    @POST(REVIEW_END_POINT)
    suspend fun createReview(@Body body: CreateReviewRequest): Response<BaseResponse>

    @GET("${URL.GET_REVIEW_END_POINT}/{id}")
    suspend fun getReviews(@Path("id") id: String): Response<GetReviewsResponse>
}