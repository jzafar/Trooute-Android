package com.travel.trooute.domain.usecase.review

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.review.request.CreateReviewRequest
import com.travel.trooute.domain.repository.ReviewRepository
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(request: CreateReviewRequest): Resource<BaseResponse> {
        return reviewRepository.createReview(request = request)
    }
}