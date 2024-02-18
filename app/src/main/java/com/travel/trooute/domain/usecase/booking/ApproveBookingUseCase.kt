package com.travel.trooute.domain.usecase.booking

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class ApproveBookingUseCase @Inject constructor(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(bookingId: String?): Resource<BaseResponse> {
        return bookingRepository.approveBooking(bookingID = bookingId)
    }
}