package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.BadgeService

class BadgeRemoteDataSourceImpl(private val api: BadgeService): BaseRepository(), BadgeRemoteDataSource {
    override suspend fun getBadges(): Result<BaseResponse> {
        return safeApiCall() { api.getBadges().body()!! }
    }

    override suspend fun changeRepresentativeBadge(badgeIdx: Int): Result<BaseResponse> {
        return safeApiCall { api.changeRepresentativeBadge(badgeIdx).body()!! }
    }

    override suspend fun getMonthBadge(): Result<BaseResponse> {
        return safeApiCall() { api.getMonthBadge().body()!! }
    }
}