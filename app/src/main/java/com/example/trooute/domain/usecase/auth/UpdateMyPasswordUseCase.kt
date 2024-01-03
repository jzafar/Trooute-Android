package com.example.trooute.domain.usecase.auth

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.request.UpdateMyPasswordRequest
import com.example.trooute.data.model.auth.response.AuthResponse
import com.example.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateMyPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: UpdateMyPasswordRequest) : Resource<AuthResponse> {
        return repository.updateMyPassword(request)
    }
}