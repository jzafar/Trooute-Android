package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class UpdateTripStatusUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(tripId: String, status:String): Resource<BaseResponse> {
        return tripsRepository.updateTripStatus(tripId = tripId, status = status)
    }
}