package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL.EMAIL_VERIFICATION_END_POINT
import com.example.trooute.core.util.URL.FORGOT_PASSWORD_END_POINT
import com.example.trooute.core.util.URL.LOGIN_END_POINT
import com.example.trooute.core.util.URL.RESEND_EMAIL_VERIFICATION_END_POINT
import com.example.trooute.core.util.URL.SIGNUP_END_POINT
import com.example.trooute.core.util.URL.UPDATE_MY_PASSWORD_END_POINT
import com.example.trooute.core.util.URL.UPDATE_PROFILE_END_POINT
import com.example.trooute.data.model.auth.request.EmailVerificationRequest
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.data.model.auth.request.LoginRequest
import com.example.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.example.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

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
}