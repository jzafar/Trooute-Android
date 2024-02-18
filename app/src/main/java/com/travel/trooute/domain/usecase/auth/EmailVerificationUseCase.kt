package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.EmailVerificationRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class EmailVerificationUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: EmailVerificationRequest) : Resource<AuthResponse> {
        return authRepository.emailVerification(request)
    }
}