package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(myInfoUserModel: MyInfoUserModel): Result<BaseResponse> = repository.updateUser(myInfoUserModel)
}