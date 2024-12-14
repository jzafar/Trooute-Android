package com.travel.trooute.data.datasource.network

import com.travel.trooute.core.util.URL.EMAIL_VERIFICATION_END_POINT
import com.travel.trooute.core.util.URL.FORGOT_PASSWORD_END_POINT
import com.travel.trooute.core.util.URL.GET_ME_END_POINT
import com.travel.trooute.core.util.URL.LOGIN_END_POINT
import com.travel.trooute.core.util.URL.LOGOUT_END_POINT
import com.travel.trooute.core.util.URL.RESEND_EMAIL_VERIFICATION_END_POINT
import com.travel.trooute.core.util.URL.SIGNUP_END_POINT
import com.travel.trooute.core.util.URL.UPDATE_DEVICEID_END_POINT
import com.travel.trooute.core.util.URL.UPDATE_MY_PASSWORD_END_POINT
import com.travel.trooute.core.util.URL.UPDATE_PROFILE_END_POINT
import com.travel.trooute.data.model.auth.request.EmailVerificationRequest
import com.travel.trooute.data.model.auth.request.ForgotPasswordRequest
import com.travel.trooute.data.model.auth.request.LoginRequest
import com.travel.trooute.data.model.auth.request.LogoutRequest
import com.travel.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.travel.trooute.data.model.auth.request.UpdateDeviceIdRequest
import com.travel.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.auth.response.DeviceIdResponse
import com.travel.trooute.data.model.common.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthAPI {
    @POST(LOGIN_END_POINT)
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST(SIGNUP_END_POINT)
    suspend fun signup(@Body body: MultipartBody): Response<AuthResponse>

    @POST(FORGOT_PASSWORD_END_POINT)
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<AuthResponse>

    @POST(EMAIL_VERIFICATION_END_POINT)
    suspend fun emailVerification(@Body body: EmailVerificationRequest): Response<AuthResponse>

    @POST(RESEND_EMAIL_VERIFICATION_END_POINT)
    suspend fun resendVerificationCode(@Body body: ResendVerificationCodeRequest): Response<AuthResponse>

    @PATCH(UPDATE_PROFILE_END_POINT)
    suspend fun updateMyProfile(@Body body: MultipartBody): Response<AuthResponse>

    @PATCH(UPDATE_MY_PASSWORD_END_POINT)
    suspend fun updateMyPassword(@Body body: UpdateMyPasswordRequest): Response<AuthResponse>
    @GET(GET_ME_END_POINT)
    suspend fun getMe(): Response<AuthResponse>
    @POST(UPDATE_DEVICEID_END_POINT)
    suspend fun updateDeviceId(@Body body: UpdateDeviceIdRequest): Response<DeviceIdResponse>

    @POST(LOGOUT_END_POINT)
    suspend fun logout(@Body body: LogoutRequest): Response<BaseResponse>

}