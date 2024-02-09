package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL.CREATE_TRIPS_END_POINT
import com.example.trooute.core.util.URL.TRIPS_HISTORY_END_POINT
import com.example.trooute.core.util.URL.GET_TRIPS_DETAILS_END_POINT
import com.example.trooute.core.util.URL.GET_TRIPS_END_POINT
import com.example.trooute.core.util.URL.UPDATE_TRIP_STATUS
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.data.model.trip.response.GetTripsResponse
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
        @Query("fromCoordinates[0]") fromLatitude: Double?,
        @Query("fromCoordinates[1]") fromLongitude: Double?,
        @Query("departureDate") departureDate: String?,
    ): Response<GetTripsResponse>

    @GET(GET_TRIPS_END_POINT)
    suspend fun getSearchedTrips(
        @Query("fromCoordinates[0]") fromLatitude: Double?,
        @Query("fromCoordinates[1]") fromLongitude: Double?,
        @Query("whereToCoordinates[0]") whereToLatitude: Double?,
        @Query("whereToCoordinates[1]") whereToLongitude: Double?
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
}