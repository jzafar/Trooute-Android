package com.travel.trooute.domain.usecase.connectPayments

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.ConnectPaymentsRepository
import javax.inject.Inject

class ConnectStripeUseCase @Inject constructor(private val repository: ConnectPaymentsRepository) {
    suspend operator fun invoke(): Resource<AuthResponse> {
        return repository.connectStripe()
    }
}