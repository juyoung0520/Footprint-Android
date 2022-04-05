package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SimpleUser
import com.footprint.footprint.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend fun invoke(user: SimpleUser): Result<SimpleUser> = repository.updateUser(user)
}