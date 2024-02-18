package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.review.request.CreateReviewRequest
import com.travel.trooute.data.datasource.network.ReviewAPI
import com.travel.trooute.data.model.review.response.GetReviewsResponse
import com.travel.trooute.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewAPI: ReviewAPI,
    private val ioDispatcher: CoroutineDispatcher
) : ReviewRepository {
    override suspend fun createReview(request: CreateReviewRequest): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                reviewAPI.createReview(request)
            }
        }
    }

    override suspend fun getReviews(userId: String): Resource<GetReviewsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                reviewAPI.getReviews(userId)
            }
        }
    }
}