package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.repository.UserRepository

//Simple User Model: 홈, 산책, 마이페이지에서 사용하는 유저 정보
class GetSimpleUserUseCase(private val repository: UserRepository) {
    suspend fun invoke(): Result<SimpleUserModel> = repository.getUser()
}