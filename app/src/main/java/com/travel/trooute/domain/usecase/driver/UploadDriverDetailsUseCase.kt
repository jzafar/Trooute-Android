package com.travel.trooute.domain.usecase.driver

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.domain.repository.DriverRepository
import javax.inject.Inject

class UploadDriverDetailsUseCase @Inject constructor(private val repository: DriverRepository) {
    suspend operator fun invoke(request: UploadDriverDetailsRequest): Resource<BaseResponse> {
        return repository.uploadDriverDetails(request = request)
    }
}