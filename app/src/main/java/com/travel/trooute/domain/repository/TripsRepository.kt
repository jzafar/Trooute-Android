package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.trip.request.CreateTripRequest
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.data.model.trip.response.GetTripDetailsResponse
import com.travel.trooute.data.model.trip.response.GetTripsResponse

interface TripsRepository {
    suspend fun createTrip(request: CreateTripRequest): Resource<BaseResponse>
    suspend fun getTrips(
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?
    ): Resource<GetTripsResponse>

    suspend fun getSearchedTrips(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double, currentDate: String
    ): Resource<GetTripsResponse>

    suspend fun getTripsDetails(tripId: String): Resource<GetTripDetailsResponse>
    suspend fun tripsHistory(): Resource<GetTripsResponse>
    suspend fun updateTripStatus(tripId: String, status: String): Resource<BaseResponse>
    suspend fun getPickupStatus(tripId: String): Resource<GetTripDetailsResponse>
    suspend fun updatePickupStatus(request: UpdatePickupStatusRequest): Resource<GetTripDetailsResponse>
}