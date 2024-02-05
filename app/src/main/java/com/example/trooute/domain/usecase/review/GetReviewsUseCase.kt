package com.example.trooute.domain.usecase.review

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.example.trooute.data.model.review.response.GetReviewsResponse
import com.example.trooute.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(private val reviewsRepository: ReviewRepository) {
    suspend operator fun invoke(userId: String): Resource<GetReviewsResponse> {
        return reviewsRepository.getReviews(userId)
    }
}