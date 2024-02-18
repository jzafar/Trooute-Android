package com.travel.trooute.domain.usecase.review

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.review.response.GetReviewsResponse
import com.travel.trooute.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(private val reviewsRepository: ReviewRepository) {
    suspend operator fun invoke(userId: String): Resource<GetReviewsResponse> {
        return reviewsRepository.getReviews(userId)
    }
}