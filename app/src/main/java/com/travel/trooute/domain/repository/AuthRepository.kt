package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
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

interface AuthRepository {
    suspend fun signIn(request: LoginRequest): Resource<AuthResponse>
    suspend fun signUp(request: MultipartBody): Resource<AuthResponse>
    suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<AuthResponse>
    suspend fun emailVerification(request: EmailVerificationRequest): Resource<AuthResponse>
    suspend fun resendVerificationCode(request: ResendVerificationCodeRequest): Resource<AuthResponse>
    suspend fun updateMyProfile(request: MultipartBody): Resource<AuthResponse>
    suspend fun updateMyPassword(request: UpdateMyPasswordRequest): Resource<AuthResponse>
    suspend fun getMe(): Resource<AuthResponse>
    suspend fun updateDeviceId(request: UpdateDeviceIdRequest): Resource<DeviceIdResponse>
    suspend fun logout(request: LogoutRequest): Resource<BaseResponse>
}