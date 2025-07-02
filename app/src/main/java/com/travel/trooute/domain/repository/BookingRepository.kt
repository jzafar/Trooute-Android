package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.bookings.request.ConfirmBookingsRequest
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.travel.trooute.data.model.bookings.response.GetBookingsResponse
import com.travel.trooute.data.model.common.BaseResponse

interface BookingRepository {
    suspend fun createBooking(createBookingRequest: CreateBookingRequest): Resource<BaseResponse>

    suspend fun getBookings(): Resource<GetBookingsResponse>

    suspend fun getBookingDetails(bookingID: String?): Resource<GetBookingDetailResponse>

    suspend fun approveBooking(bookingID: String?): Resource<BaseResponse>

    suspend fun confirmBooking(bookingID: String, confirmBookingsRequest: ConfirmBookingsRequest): Resource<BaseResponse>

    suspend fun cancelBooking(bookingID: String?): Resource<BaseResponse>

    suspend fun completeBooking(bookingID: String?): Resource<BaseResponse>

    suspend fun paymentSuccess(url: String?): Resource<BaseResponse>
}