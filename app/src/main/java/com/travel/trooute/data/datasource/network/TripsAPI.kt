package com.travel.trooute.data.datasource.network

import com.travel.trooute.core.util.URL.CREATE_TRIPS_END_POINT
import com.travel.trooute.core.util.URL.GET_PICKUP_PASSENGERS_STATUS
import com.travel.trooute.core.util.URL.TRIPS_HISTORY_END_POINT
import com.travel.trooute.core.util.URL.GET_TRIPS_DETAILS_END_POINT
import com.travel.trooute.core.util.URL.GET_TRIPS_END_POINT
import com.travel.trooute.core.util.URL.UPDATE_PICKUP_PASSENGERS_STATUS
import com.travel.trooute.core.util.URL.UPDATE_TRIP_STATUS
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.trip.request.CreateTripRequest
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.data.model.trip.response.GetTripDetailsResponse
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TripsAPI {
    @POST(CREATE_TRIPS_END_POINT)
    suspend fun createTrip(@Body body: CreateTripRequest): Response<BaseResponse>

    @GET(GET_TRIPS_END_POINT)
    suspend fun getTrips(
        @Query("fromCoordinates[0]") fromLongitude: Double?,
        @Query("fromCoordinates[1]") fromLatitude: Double?,
        @Query("departureDate") departureDate: String?,
        @Query("fetchAll") fetchAll: Boolean,
    ): Response<GetTripsResponse>

    @GET(GET_TRIPS_END_POINT)
    suspend fun getSearchedTrips(
        @Query("fromCoordinates[0]") fromLongitude: Double?,
        @Query("fromCoordinates[1]") fromLatitude: Double?,
        @Query("whereToCoordinates[0]") whereToLongitude: Double?,
        @Query("whereToCoordinates[1]") whereToLatitude: Double?,
        @Query("currentDate") currentDate: String?,
        @Query("flexibleDays") flexibleDays: Int?,
        @Query("toRange") toRange: Int?,
        @Query("fromRange") fromRange: Int?,
    ): Response<GetTripsResponse>

    @GET("$GET_TRIPS_DETAILS_END_POINT/{id}")
    suspend fun getTripsDetails(@Path("id") tripId: String?): Response<GetTripDetailsResponse>

    @GET(TRIPS_HISTORY_END_POINT)
    suspend fun tripsHistory(): Response<GetTripsResponse>

    @PATCH("$UPDATE_TRIP_STATUS/{id}")
    suspend fun updateTripStatus(
        @Path("id") tripId: String?,
        @Query("status") status: String?
    ): Response<BaseResponse>
    @GET("$GET_PICKUP_PASSENGERS_STATUS/{id}")
    suspend fun getPickupStatus(@Path("id") tripId: String): Response<GetTripDetailsResponse>
    @POST(UPDATE_PICKUP_PASSENGERS_STATUS)
    suspend fun updatePickupStatus(@Body body: UpdatePickupStatusRequest): Response<GetTripDetailsResponse>
}