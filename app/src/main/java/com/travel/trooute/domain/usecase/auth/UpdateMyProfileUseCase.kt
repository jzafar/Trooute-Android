package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.domain.repository.AuthRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: MultipartBody) : Resource<AuthResponse> {
        return repository.updateMyProfile(request)
    }
}