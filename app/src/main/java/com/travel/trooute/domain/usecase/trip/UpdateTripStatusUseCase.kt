package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class UpdateTripStatusUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(tripId: String, status:String): Resource<BaseResponse> {
        return tripsRepository.updateTripStatus(tripId = tripId, status = status)
    }
}