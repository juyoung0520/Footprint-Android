package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.dto.MonthBadgeResponse
import com.footprint.footprint.domain.repository.BadgeRepository

class GetMonthBadgeUseCase(private val repository: BadgeRepository) {
    suspend fun invoke(): Result<MonthBadgeResponse> = repository.getMonthBadge()
}