package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.trip.request.CreateTripRequest
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class CreateTripUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(request: CreateTripRequest): Resource<BaseResponse> {
        return tripsRepository.createTrip(request = request)
    }
}