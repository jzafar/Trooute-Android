package com.example.trooute.domain.usecase.auth

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.SignupRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.repository.AuthRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: MultipartBody): Resource<AuthResponse>{
        return authRepository.signUp(request)
    }
}