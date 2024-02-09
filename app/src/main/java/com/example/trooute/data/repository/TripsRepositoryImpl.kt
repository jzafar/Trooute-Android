package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.data.model.trip.response.GetTripsResponse
import com.example.trooute.data.datasource.network.TripsAPI
import com.example.trooute.domain.repository.TripsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?
    ): Resource<GetTripsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getTrips(
                    fromLatitude = fromLatitude,
                    fromLongitude = fromLongitude,
                    departureDate = departureDate
                )
            }
        }
    }

    override suspend fun getSearchedTrips(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double,
    ): Resource<GetTripsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                tripsAPI.getSearchedTrips(
                    fromLatitude = fromLatitude,
                    fromLongitude = fromLongitude,
                    whereToLatitude = whereToLatitude,
                    whereToLongitude = whereToLongitude,
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
}