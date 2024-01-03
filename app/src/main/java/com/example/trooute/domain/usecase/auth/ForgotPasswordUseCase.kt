package com.example.trooute.domain.usecase.auth

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.ForgotPasswordRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: ForgotPasswordRequest) : Resource<AuthResponse> {
        return authRepository.forgotPassword(request)
    }
}