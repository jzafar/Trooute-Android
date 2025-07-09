package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetTripsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?, fetchAll: Boolean
    ): Resource<GetTripsResponse> {
        return tripsRepository.getTrips(
            fromLatitude = fromLatitude, fromLongitude = fromLongitude, departureDate = departureDate, fetchAll = fetchAll
        )
    }
}