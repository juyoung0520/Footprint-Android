package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.InitUserModel
import com.footprint.footprint.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(initUserModel: InitUserModel): Result<BaseResponse> = repository.registerUser(initUserModel)
}