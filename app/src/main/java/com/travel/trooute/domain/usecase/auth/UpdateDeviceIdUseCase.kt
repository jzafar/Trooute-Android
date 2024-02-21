package com.travel.trooute.domain.usecase.auth

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.request.ForgotPasswordRequest
import com.travel.trooute.data.model.auth.request.UpdateDeviceIdRequest
import com.travel.trooute.data.model.auth.response.AuthResponse
import com.travel.trooute.data.model.auth.response.DeviceIdResponse
import com.travel.trooute.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateDeviceIdUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(request: UpdateDeviceIdRequest) : Resource<DeviceIdResponse> {
        return authRepository.updateDeviceId(request)
    }
}