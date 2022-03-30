package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(myInfoUserModel: MyInfoUserModel): Result<BaseResponse> = repository.updateUser(myInfoUserModel)
}