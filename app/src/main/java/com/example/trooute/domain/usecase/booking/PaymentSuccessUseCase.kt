package com.example.trooute.domain.usecase.booking

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class PaymentSuccessUseCase @Inject constructor(private val repository: BookingRepository) {
    suspend operator fun invoke(url: String?): Resource<BaseResponse> {
        return repository.paymentSuccess(url)
    }
}