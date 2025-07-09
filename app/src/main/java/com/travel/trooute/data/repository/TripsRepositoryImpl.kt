package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.trip.request.CreateTripRequest
import com.travel.trooute.data.model.trip.response.GetTripDetailsResponse
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.data.datasource.network.TripsAPI
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.domain.repository.TripsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import javax.inject.Inject

class TripsRepositoryImpl @Inject constructor(
    private val tripsAPI: TripsAPI,
    private val ioDispatcher: CoroutineDispatcher
) : TripsRepository {
    override suspend fun createTrip(request: CreateTripRequest): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.createTrip(body = request)
            }
        }
    }

    override suspend fun getTrips(
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?, fetchAll: Boolean
    ): Resource<GetTripsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getTrips(
                    fromLatitude = fromLatitude,
                    fromLongitude = fromLongitude,
                    departureDate = departureDate,
                    fetchAll = fetchAll
                )
            }
        }
    }

    override suspend fun getSearchedTrips(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double, currentDate: String, flexibleDays: Int?,
        toRange: Int?, fromRange: Int?
    ): Resource<GetTripsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getSearchedTrips(
                    fromLatitude = fromLatitude,
                    fromLongitude = fromLongitude,
                    whereToLatitude = whereToLatitude,
                    whereToLongitude = whereToLongitude,
                    currentDate = currentDate,
                    flexibleDays = flexibleDays,
                    toRange = toRange,
                    fromRange = fromRange

                )
            }
        }
    }

    override suspend fun getTripsDetails(tripId: String): Resource<GetTripDetailsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getTripsDetails(tripId = tripId)
            }
        }
    }

    override suspend fun tripsHistory(): Resource<GetTripsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.tripsHistory()
            }
        }
    }

    override suspend fun updateTripStatus(tripId: String, status: String): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.updateTripStatus(tripId = tripId, status)
            }
        }
    }

    override suspend fun getPickupStatus(tripId: String): Resource<GetTripDetailsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getPickupStatus(tripId = tripId)
            }
        }
    }
    override suspend fun updatePickupStatus(@Body request: UpdatePickupStatusRequest): Resource<GetTripDetailsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.updatePickupStatus(body = request)
            }
        }
    }
}