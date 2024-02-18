package com.travel.trooute.domain.usecase.booking

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.travel.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingDetailsUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(bookingId: String?): Resource<GetBookingDetailResponse> {
        return bookingRepository.getBookingDetails(bookingID = bookingId)
    }
}