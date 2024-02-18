package com.travel.trooute.domain.usecase.driver

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.travel.trooute.domain.repository.DriverRepository
import javax.inject.Inject

class GetDriverRequestUseCase @Inject constructor(private val repository: DriverRepository) {
    suspend operator fun invoke(): Resource<GetDriversRequestsResponse> {
        return repository.getDriverRequest()
    }
}