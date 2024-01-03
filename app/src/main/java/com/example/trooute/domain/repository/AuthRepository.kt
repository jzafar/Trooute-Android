package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.EmailVerificationRequest
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.data.model.auth.request.LoginRequest
import com.example.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.example.trooute.data.model.auth.request.SignupRequest
import com.example.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import okhttp3.MultipartBody

interface AuthRepository {
    suspend fun signIn(request: LoginRequest): Resource<AuthResponse>
    suspend fun signUp(request: MultipartBody): Resource<AuthResponse>
    suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<AuthResponse>
    suspend fun emailVerification(request: EmailVerificationRequest): Resource<AuthResponse>
    suspend fun resendVerificationCode(request: ResendVerificationCodeRequest): Resource<AuthResponse>
    suspend fun updateMyProfile(request: MultipartBody): Resource<AuthResponse>
    suspend fun updateMyPassword(request: UpdateMyPasswordRequest): Resource<AuthResponse>
}