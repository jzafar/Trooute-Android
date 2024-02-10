package com.example.trooute.domain.usecase.driver

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.example.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.example.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse
import com.example.trooute.domain.repository.DriverRepository
import javax.inject.Inject

class UpdateCarInfoRequestUseCase @Inject constructor(private val repository: DriverRepository) {
    suspend operator fun invoke(request: UploadDriverDetailsRequest): Resource<UpdateCarInfoRequestsResponse> {
        return repository.updateCarInfoDetails(request = request)
    }
}