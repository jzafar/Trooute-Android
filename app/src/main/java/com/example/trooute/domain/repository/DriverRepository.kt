package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.example.trooute.data.model.driver.response.GetDriversRequestsResponse

interface DriverRepository {
    suspend fun uploadDriverDetails(request: UploadDriverDetailsRequest): Resource<BaseResponse>
    suspend fun switchDriverMode(): Resource<BaseResponse>
    suspend fun getDriverRequest(): Resource<GetDriversRequestsResponse>
    suspend fun approveDriver(driverId: String?): Resource<BaseResponse>
}