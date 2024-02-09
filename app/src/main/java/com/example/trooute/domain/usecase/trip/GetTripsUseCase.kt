package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripsResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetTripsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(
        fromLatitude: Double?, fromLongitude: Double?, departureDate: String?
    ): Resource<GetTripsResponse> {
        return tripsRepository.getTrips(
            fromLatitude = fromLatitude, fromLongitude = fromLongitude, departureDate = departureDate
        )
    }
}