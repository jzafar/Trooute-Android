package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.trip.request.CreateTripRequest
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class CreateTripUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(request: CreateTripRequest): Resource<BaseResponse> {
        return tripsRepository.createTrip(request = request)
    }
}