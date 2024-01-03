package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripsResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetSearchedTripsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double,
    ): Resource<GetTripsResponse> {
        return tripsRepository.getSearchedTrips(
            fromLatitude = fromLatitude, fromLongitude = fromLongitude,
            whereToLatitude = whereToLatitude, whereToLongitude = whereToLongitude
        )
    }
}