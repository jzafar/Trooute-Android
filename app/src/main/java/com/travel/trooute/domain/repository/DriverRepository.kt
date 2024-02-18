package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.travel.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse

interface DriverRepository {
    suspend fun uploadDriverDetails(request: UploadDriverDetailsRequest): Resource<BaseResponse>
    suspend fun switchDriverMode(): Resource<BaseResponse>
    suspend fun getDriverRequest(): Resource<GetDriversRequestsResponse>
    suspend fun approveDriver(driverId: String?): Resource<BaseResponse>
    suspend fun updateCarInfoDetails(request: UploadDriverDetailsRequest): Resource<UpdateCarInfoRequestsResponse>
}