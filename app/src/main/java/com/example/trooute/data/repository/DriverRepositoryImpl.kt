package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.UploadMultipart.imgFileMultipartBody
import com.example.trooute.core.util.UploadMultipart.stringRequestBody
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.example.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.example.trooute.data.datasource.network.DriverAPI
import com.example.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse
import com.example.trooute.domain.repository.DriverRepository
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