package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.data.datasource.network.ReviewAPI
import com.example.trooute.domain.repository.ReviewRepository
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
}