package com.travel.trooute.data.datasource.network

import com.travel.trooute.core.util.URL.APPROVE_BOOKING_END_POINT
import com.travel.trooute.core.util.URL.CANCEL_BOOKING_END_POINT
import com.travel.trooute.core.util.URL.COMPLETE_BOOKING_END_POINT
import com.travel.trooute.core.util.URL.CONFIRM_BOOKING_END_POINT
import com.travel.trooute.core.util.URL.CREATE_BOOKING_END_POINT
import com.travel.trooute.core.util.URL.GET_BOOKING_DETAILS_END_POINT
import com.travel.trooute.core.util.URL.GET_BOOKING_END_POINT
import com.travel.trooute.data.model.bookings.request.CreateBookingRequest
import com.travel.trooute.data.model.bookings.response.GetBookingDetailResponse
import com.travel.trooute.data.model.bookings.response.GetBookingsResponse
import com.travel.trooute.data.model.common.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface BookingsAPI {
    @POST(CREATE_BOOKING_END_POINT)
    suspend fun createBooking(@Body body: CreateBookingRequest): Response<BaseResponse>

    @GET(GET_BOOKING_END_POINT)
    suspend fun getBookings(): Response<GetBookingsResponse>

    @GET("$GET_BOOKING_DETAILS_END_POINT/{id}")
    suspend fun getBookingDetails(@Path("id") id: String?): Response<GetBookingDetailResponse>

    @POST("$APPROVE_BOOKING_END_POINT/{id}/approve")
    suspend fun approveBooking(@Path("id") id: String?): Response<BaseResponse>

    @POST("$CONFIRM_BOOKING_END_POINT/{id}/confirm")
    suspend fun confirmBooking(@Path("id") id: String?): Response<BaseResponse>

    @POST("$CANCEL_BOOKING_END_POINT/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String?): Response<BaseResponse>

    @POST("$COMPLETE_BOOKING_END_POINT/{id}/complete")
    suspend fun completeBooking(@Path("id") id: String?): Response<BaseResponse>

    @GET
    suspend fun paymentSuccess(@Url url: String?) : Response<BaseResponse>
}