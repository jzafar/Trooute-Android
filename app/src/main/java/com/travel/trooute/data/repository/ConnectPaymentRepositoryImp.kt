package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.datasource.network.ConnectPaymentsAPI
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.connectPayments.ConnectPaypalRequest
import com.travel.trooute.domain.repository.ConnectPaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConnectPaymentRepositoryImp @Inject constructor(
    private val connectPaymentsAPI: ConnectPaymentsAPI,
    private val ioDispatcher: CoroutineDispatcher
) : ConnectPaymentsRepository {
    override suspend fun connectPayPal(request: ConnectPaypalRequest): Resource<AuthResponse>{
        return withContext(ioDispatcher) {
            safeApiCall {
                connectPaymentsAPI.connectPayPal(request)
            }
        }
    }
    override suspend fun connectStripe(): Resource<AuthResponse>{
        return withContext(ioDispatcher) {
            safeApiCall {
                connectPaymentsAPI.connectStripe()
            }
        }
    }
}

