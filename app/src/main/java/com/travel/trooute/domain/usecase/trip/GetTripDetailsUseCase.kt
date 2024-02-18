package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripDetailsResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetTripDetailsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(tripId: String): Resource<GetTripDetailsResponse> {
        return tripsRepository.getTripsDetails(tripId = tripId)
    }
}