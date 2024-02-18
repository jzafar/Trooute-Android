package com.travel.trooute.domain.usecase.driver

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.common.BaseResponse
import com.travel.trooute.domain.repository.DriverRepository
import javax.inject.Inject

class SwitchDriverModeUseCase @Inject constructor(private val repository: DriverRepository) {
    suspend operator fun invoke(): Resource<BaseResponse> {
        return repository.switchDriverMode()
    }
}