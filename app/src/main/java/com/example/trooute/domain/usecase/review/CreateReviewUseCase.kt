package com.example.trooute.domain.usecase.review

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.review.request.CreateReviewRequest
import com.example.trooute.domain.repository.ReviewRepository
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(request: CreateReviewRequest): Resource<BaseResponse> {
        return reviewRepository.createReview(request = request)
    }
}