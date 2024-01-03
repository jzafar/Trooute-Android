package com.example.trooute.domain.usecase.booking

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class CompleteBookingUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(bookingId: String?): Resource<BaseResponse> {
        return bookingRepository.completeBooking(bookingID = bookingId)
    }
}