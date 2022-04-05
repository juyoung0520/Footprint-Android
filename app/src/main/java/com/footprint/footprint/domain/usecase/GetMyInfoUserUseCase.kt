package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.repository.UserRepository

//MyInfoUserModel: 내 정보
class GetMyInfoUserUseCase(private val repository: UserRepository) {
    suspend fun invoke(): Result<MyInfoUserModel> = repository.getMyInfoUser()
}