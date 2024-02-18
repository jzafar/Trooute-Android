package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.travel.trooute.data.model.bookings.response.GetBookingsResponse
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.datasource.network.BookingsAPI
import com.travel.trooute.domain.repository.BookingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val bookingsAPI: BookingsAPI,
    private val ioDispatcher: CoroutineDispatcher
) : BookingRepository {
    override suspend fun createBooking(createBookingRequest: CreateBookingRequest): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.createBooking(body = createBookingRequest)
            }
        }
    }

    override suspend fun getBookings(): Resource<GetBookingsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.getBookings()
            }
        }
    }

    override suspend fun getBookingDetails(bookingID: String?): Resource<GetBookingDetailResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.getBookingDetails(id = bookingID)
            }
        }
    }

    override suspend fun approveBooking(bookingID: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.approveBooking(id = bookingID)
            }
        }
    }

    override suspend fun confirmBooking(bookingID: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.confirmBooking(id = bookingID)
            }
        }
    }

    override suspend fun cancelBooking(bookingID: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.cancelBooking(id = bookingID)
            }
        }
    }

    override suspend fun completeBooking(bookingID: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.completeBooking(id = bookingID)
            }
        }
    }

    override suspend fun paymentSuccess(url: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                bookingsAPI.paymentSuccess(url)
            }
        }
    }
}