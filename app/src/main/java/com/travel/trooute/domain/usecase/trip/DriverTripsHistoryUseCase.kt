package com.travel.trooute.domain.usecase.trip

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.trip.response.GetTripsResponse
import com.travel.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class DriverTripsHistoryUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(): Resource<GetTripsResponse> {
        return tripsRepository.tripsHistory()
    }
}