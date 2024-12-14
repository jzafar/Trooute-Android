package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.model.auth.request.EmailVerificationRequest
import com.travel.trooute.data.model.auth.request.ForgotPasswordRequest
import com.travel.trooute.data.model.auth.request.LoginRequest
import com.travel.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.datasource.network.AuthAPI
import com.travel.trooute.data.model.auth.request.LogoutRequest
import com.travel.trooute.data.model.auth.request.UpdateDeviceIdRequest
import com.travel.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.travel.trooute.data.model.auth.response.DeviceIdResponse
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authAPI: AuthAPI,
    private val ioDispatcher:CoroutineDispatcher
) : AuthRepository{
    override suspend fun signIn(request: LoginRequest): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.login(request)
            }
        }
    }

    override suspend fun logout(request: LogoutRequest): Resource<BaseResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.logout(request)
            }
        }
    }

    override suspend fun signUp(request: MultipartBody): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.signup(request)
            }
        }
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.forgotPassword(request)
            }
        }
    }

    override suspend fun emailVerification(request: EmailVerificationRequest): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.emailVerification(request)
            }
        }
    }

    override suspend fun resendVerificationCode(request: ResendVerificationCodeRequest): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.resendVerificationCode(request)
            }
        }
    }

    override suspend fun updateMyProfile(request: MultipartBody): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.updateMyProfile(request)
            }
        }
    }

    override suspend fun updateMyPassword(request: UpdateMyPasswordRequest): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.updateMyPassword(request)
            }
        }
    }

    override suspend fun getMe(): Resource<AuthResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.getMe()
            }
        }
    }

    override suspend fun updateDeviceId(request: UpdateDeviceIdRequest): Resource<DeviceIdResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                authAPI.updateDeviceId(request)
            }
        }
    }
}