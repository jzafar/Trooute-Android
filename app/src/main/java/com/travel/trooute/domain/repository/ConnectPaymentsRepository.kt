package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.connectPayments.ConnectPaypalRequest


interface ConnectPaymentsRepository {
    suspend fun connectPayPal(request: ConnectPaypalRequest): Resource<AuthResponse>
    suspend fun connectStripe(): Resource<AuthResponse>
}