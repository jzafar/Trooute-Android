package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.example.trooute.data.model.trip.response.GetTripDetailsResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class UpdatePickupStatusUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(request: UpdatePickupStatusRequest): Resource<GetTripDetailsResponse> {
        return tripsRepository.updatePickupStatus(request = request)
    }
}