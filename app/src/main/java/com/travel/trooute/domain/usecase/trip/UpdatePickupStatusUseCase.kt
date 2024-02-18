package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.data.model.trip.response.GetTripDetailsResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class UpdatePickupStatusUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(request: UpdatePickupStatusRequest): Resource<GetTripDetailsResponse> {
        return tripsRepository.updatePickupStatus(request = request)
    }
}