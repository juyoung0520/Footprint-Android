package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.MonthBadgeResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.BadgeInfo

interface BadgeRepository {
    suspend fun getBadges(): Result<BadgeInfo>
    suspend fun changeRepresentativeBadge(badgeIdx: Int): Result<Badge>
    suspend fun getMonthBadge(): Result<MonthBadgeResponse>
}