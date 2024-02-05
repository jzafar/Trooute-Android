package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.UploadMultipart.imgFileMultipartBody
import com.example.trooute.core.util.UploadMultipart.stringRequestBody
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.model.auth.request.EmailVerificationRequest
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.data.model.auth.request.LoginRequest
import com.example.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.example.trooute.data.model.auth.request.SignupRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.data.datasource.network.AuthAPI
import com.example.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.example.trooute.domain.repository.AuthRepository
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
}