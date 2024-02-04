package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.data.model.trip.response.GetTripsResponse

interface TripsRepository {
    suspend fun createTrip(request: CreateTripRequest): Resource<BaseResponse>
    suspend fun getTrips(
        fromLatitude: Double?, fromLongitude: Double?
    ): Resource<GetTripsResponse>

    suspend fun getSearchedTrips(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double,
    ): Resource<GetTripsResponse>

    suspend fun getTripsDetails(tripId: String): Resource<GetTripDetailsResponse>
    suspend fun tripsHistory(): Resource<GetTripsResponse>
    suspend fun updateTripStatus(tripId: String, status: String): Resource<BaseResponse>
}