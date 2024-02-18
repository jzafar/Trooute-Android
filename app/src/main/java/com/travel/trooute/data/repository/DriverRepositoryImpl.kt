package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.UploadMultipart.imgFileMultipartBody
import com.travel.trooute.core.util.UploadMultipart.stringRequestBody
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.travel.trooute.data.datasource.network.DriverAPI
import com.travel.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse
import com.travel.trooute.domain.repository.DriverRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val driverAPI: DriverAPI,
    private val ioDispatcher: CoroutineDispatcher
) : DriverRepository {
    override suspend fun uploadDriverDetails(request: UploadDriverDetailsRequest): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                driverAPI.uploadDriverDetails(
                    carPhoto = imgFileMultipartBody("carPhoto", request.carPhoto),
                    driverLicense = imgFileMultipartBody("driverLicense", request.driverLicense),
                    make = stringRequestBody(request.make),
                    model = stringRequestBody(request.model),
                    registrationNumber = stringRequestBody(request.registrationNumber),
                    year = stringRequestBody(request.year),
                    color = stringRequestBody(request.color)
                )
            }
        }
    }

    override suspend fun switchDriverMode(): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                driverAPI.switchDriverMode()
            }
        }
    }

    override suspend fun getDriverRequest(): Resource<GetDriversRequestsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                driverAPI.getDriverRequest()
            }
        }
    }

    override suspend fun approveDriver(driverId: String?): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                driverAPI.approveDriver(id = driverId)
            }
        }
    }

    override suspend fun updateCarInfoDetails(request: UploadDriverDetailsRequest): Resource<UpdateCarInfoRequestsResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                driverAPI.updateCarInfoDetails(
                    carPhoto = imgFileMultipartBody("carPhoto", request.carPhoto),
                    make = stringRequestBody(request.make),
                    model = stringRequestBody(request.model),
                    registrationNumber = stringRequestBody(request.registrationNumber),
                    year = stringRequestBody(request.year),
                    color = stringRequestBody(request.color)
                )
            }
        }
    }
}