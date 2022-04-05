package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.AuthRepository

class UnRegisterUseCase(private val repository: AuthRepository)  {
    suspend fun invoke(): Result<BaseResponse> = repository.unregister()
}