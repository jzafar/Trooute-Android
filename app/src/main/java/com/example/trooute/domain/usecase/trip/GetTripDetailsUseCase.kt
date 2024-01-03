package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class GetTripDetailsUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(tripId: String): Resource<GetTripDetailsResponse> {
        return tripsRepository.getTripsDetails(tripId = tripId)
    }
}