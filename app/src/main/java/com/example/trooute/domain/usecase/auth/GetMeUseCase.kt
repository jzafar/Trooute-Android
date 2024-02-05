package com.example.trooute.domain.usecase.auth

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.repository.AuthRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() : Resource<AuthResponse> {
        return repository.getMe()
    }
}