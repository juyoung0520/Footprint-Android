package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.RepresentativeBadge
import com.footprint.footprint.domain.repository.BadgeRepository

class ChangeRepresentativeBadgeUseCase(private val repository: BadgeRepository) {
    suspend fun invoke(badgeIdx: Int): Result<RepresentativeBadge> = repository.changeRepresentativeBadge(badgeIdx)
}