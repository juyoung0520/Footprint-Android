package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.MonthBadgeInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.BadgeInfo
import com.footprint.footprint.domain.model.RepresentativeBadge

interface BadgeRepository {
    suspend fun getBadges(): Result<BadgeInfo>
    suspend fun changeRepresentativeBadge(badgeIdx: Int): Result<RepresentativeBadge>
    suspend fun getMonthBadge(): Result<MonthBadgeInfoDTO>
}