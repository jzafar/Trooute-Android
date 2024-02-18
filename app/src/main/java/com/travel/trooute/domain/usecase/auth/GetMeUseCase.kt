package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() : Resource<AuthResponse> {
        return repository.getMe()
    }
}