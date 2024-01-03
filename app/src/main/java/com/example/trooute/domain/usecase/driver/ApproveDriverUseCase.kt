package com.example.trooute.domain.usecase.driver

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.domain.repository.DriverRepository
import javax.inject.Inject

class ApproveDriverUseCase @Inject constructor(private val repository: DriverRepository) {
    suspend operator fun invoke(driverId: String?): Resource<BaseResponse> {
        return repository.approveDriver(driverId = driverId)
    }
}