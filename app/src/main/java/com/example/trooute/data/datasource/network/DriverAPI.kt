package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL.APPROVE_DRIVER_END_POINT
import com.example.trooute.core.util.URL.GET_DRIVERS_REQUESTS_END_POINT
import com.example.trooute.core.util.URL.SWITCH_DRIVER_END_POINT
import com.example.trooute.core.util.URL.UPDATE_CAR_INFO_END_POINT
import com.example.trooute.core.util.URL.UPLOAD_DRIVER_END_POINT
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.driver.response.GetDriversRequestsResponse
import com.example.trooute.data.model.driver.response.UpdateCarInfoRequestsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DriverAPI {
    @Multipart
    @POST(UPLOAD_DRIVER_END_POINT)
    suspend fun uploadDriverDetails(
        @Part("make") make: RequestBody,
        @Part("model") model: RequestBody,
        @Part("registrationNumber") registrationNumber: RequestBody,
        @Part("year") year: RequestBody,
        @Part("color") color: RequestBody,
        @Part carPhoto: MultipartBody.Part,
        @Part driverLicense: MultipartBody.Part,
    ): Response<BaseResponse>

    @Multipart
    @POST(UPDATE_CAR_INFO_END_POINT)
    suspend fun updateCarInfoDetails(
        @Part("make") make: RequestBody,
        @Part("model") model: RequestBody,
        @Part("registrationNumber") registrationNumber: RequestBody,
        @Part("year") year: RequestBody,
        @Part("color") color: RequestBody,
        @Part carPhoto: MultipartBody.Part
    ): Response<UpdateCarInfoRequestsResponse>

    @PATCH(SWITCH_DRIVER_END_POINT)
    suspend fun switchDriverMode(): Response<BaseResponse>

    @GET(GET_DRIVERS_REQUESTS_END_POINT)
    suspend fun getDriverRequest(): Response<GetDriversRequestsResponse>

    @PATCH("$APPROVE_DRIVER_END_POINT/{id}/approve-driver")
    suspend fun approveDriver(@Path("id") id: String?): Response<BaseResponse>
}