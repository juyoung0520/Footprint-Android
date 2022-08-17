package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.MonthBadgeInfoDTO
import com.footprint.footprint.domain.repository.BadgeRepository

class GetMonthBadgeUseCase(private val repository: BadgeRepository) {
    suspend fun invoke(): Result<MonthBadgeInfoDTO> = repository.getMonthBadge()
}