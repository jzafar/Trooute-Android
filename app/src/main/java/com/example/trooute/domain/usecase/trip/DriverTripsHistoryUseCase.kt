package com.example.trooute.domain.usecase.trip

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.trip.response.GetTripsResponse
import com.example.trooute.domain.repository.TripsRepository
import javax.inject.Inject

class DriverTripsHistoryUseCase @Inject constructor(private val tripsRepository: TripsRepository) {
    suspend operator fun invoke(): Resource<GetTripsResponse> {
        return tripsRepository.tripsHistory()
    }
}