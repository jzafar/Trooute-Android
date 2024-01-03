package com.example.trooute.domain.usecase.auth

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class ResendEmailVerificationCodeUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: ResendVerificationCodeRequest) : Resource<AuthResponse> {
        return authRepository.resendVerificationCode(request)
    }
}