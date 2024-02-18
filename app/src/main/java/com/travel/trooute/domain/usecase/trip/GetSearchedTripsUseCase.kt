package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetSearchedTripsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(
        fromLatitude: Double, fromLongitude: Double,
        whereToLatitude: Double, whereToLongitude: Double, currentDate: String
    ): Resource<GetTripsResponse> {
        return tripsRepository.getSearchedTrips(
            fromLatitude = fromLatitude, fromLongitude = fromLongitude,
            whereToLatitude = whereToLatitude, whereToLongitude = whereToLongitude,
            currentDate = currentDate
        )
    }
}