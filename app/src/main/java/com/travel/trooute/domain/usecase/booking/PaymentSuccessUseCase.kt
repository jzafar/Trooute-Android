package com.travel.trooute.domain.usecase.booking

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.BookingRepository
import javax.inject.Inject

class PaymentSuccessUseCase @Inject constructor(private val repository: BookingRepository) {
    suspend operator fun invoke(url: String?): Resource<BaseResponse> {
        return repository.paymentSuccess(url)
    }
}