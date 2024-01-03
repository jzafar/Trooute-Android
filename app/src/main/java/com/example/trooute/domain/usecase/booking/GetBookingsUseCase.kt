package com.example.trooute.domain.usecase.booking

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.bookings.response.GetBookingsResponse
import com.example.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(): Resource<GetBookingsResponse> {
        return bookingRepository.getBookings()
    }
}