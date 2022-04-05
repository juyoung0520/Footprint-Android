package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.Login
import com.footprint.footprint.domain.repository.AuthRepository

class AutoLoginUseCase(private val repository: AuthRepository)  {
    suspend fun invoke(): Result<Login> = repository.autoLogin()
}