package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.User
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    suspend fun invoke(): Result<User> = repository.getUser()
}