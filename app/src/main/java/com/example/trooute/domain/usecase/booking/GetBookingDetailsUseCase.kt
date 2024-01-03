package com.example.trooute.domain.usecase.booking

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.example.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingDetailsUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(bookingId: String?): Resource<GetBookingDetailResponse> {
        return bookingRepository.getBookingDetails(bookingID = bookingId)
    }
}