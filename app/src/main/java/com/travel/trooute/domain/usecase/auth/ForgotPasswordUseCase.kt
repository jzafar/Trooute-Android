package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.ForgotPasswordRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: ForgotPasswordRequest) : Resource<AuthResponse> {
        return authRepository.forgotPassword(request)
    }
}