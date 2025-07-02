package com.travel.trooute.data.datasource.network

import com.travel.trooute.core.util.URL.CONNECT_PAYPAL
import com.travel.trooute.core.util.URL.CONNECT_STRIPE
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.connectPayments.ConnectPaypalRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ConnectPaymentsAPI {
    @POST(CONNECT_PAYPAL)
    suspend fun connectPayPal(@Body body: ConnectPaypalRequest): Response<AuthResponse>
    @POST(CONNECT_STRIPE)
    suspend fun connectStripe(): Response<AuthResponse>
}