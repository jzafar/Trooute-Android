package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.ResendVerificationCodeRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class ResendEmailVerificationCodeUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: ResendVerificationCodeRequest) : Resource<AuthResponse> {
        return authRepository.resendVerificationCode(request)
    }
}