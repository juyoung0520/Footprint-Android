package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.UserModel
import com.footprint.footprint.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userModel: UserModel): Result<BaseResponse> = repository.registerUser(userModel)
}