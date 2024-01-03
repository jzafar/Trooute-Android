package com.example.trooute.domain.usecase.booking

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.bookings.request.CreateBookingRequest
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(request: CreateBookingRequest): Resource<BaseResponse> {
        return bookingRepository.createBooking(createBookingRequest = request)
    }
}