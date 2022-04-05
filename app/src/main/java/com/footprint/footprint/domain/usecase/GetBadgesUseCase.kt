package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BadgeInfo
import com.footprint.footprint.domain.repository.BadgeRepository

class GetBadgesUseCase(private val repository: BadgeRepository) {
    suspend fun invoke(): Result<BadgeInfo> = repository.getBadges()
}