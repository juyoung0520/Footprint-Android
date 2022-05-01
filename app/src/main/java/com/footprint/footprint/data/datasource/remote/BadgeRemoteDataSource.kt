package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result

interface BadgeRemoteDataSource {
    suspend fun getBadges(): Result<BaseResponse>
    suspend fun changeRepresentativeBadge(badgeIdx: Int): Result<BaseResponse>
    suspend fun getMonthBadge(): Result<BaseResponse>
}
