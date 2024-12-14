package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.LoginRequest
import com.travel.trooute.data.model.auth.request.LogoutRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: LoginRequest): Resource<AuthResponse>{
        return authRepository.signIn(request)
    }
}