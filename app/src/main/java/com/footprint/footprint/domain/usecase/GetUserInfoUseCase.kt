package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.UserInfoDTO
import com.footprint.footprint.domain.repository.AchieveRepository


class GetUserInfoUseCase(private val repository: AchieveRepository) {
    suspend operator fun invoke(): Result<UserInfoDTO> = repository.getUserInfo()
}