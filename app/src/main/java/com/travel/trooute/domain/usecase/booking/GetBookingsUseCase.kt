package com.travel.trooute.domain.usecase.booking

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.bookings.response.GetBookingsResponse
import com.travel.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(): Resource<GetBookingsResponse> {
        return bookingRepository.getBookings()
    }
}