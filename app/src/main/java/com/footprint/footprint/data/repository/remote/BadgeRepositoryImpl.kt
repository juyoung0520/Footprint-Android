package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.BadgeRemoteDataSource
import com.footprint.footprint.data.dto.MonthBadgeInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.BadgeInfo
import com.footprint.footprint.domain.model.RepresentativeBadge
import com.footprint.footprint.domain.repository.BadgeRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils

class BadgeRepositoryImpl(private val dataSource: BadgeRemoteDataSource): BadgeRepository {
    override suspend fun getBadges(): Result<BadgeInfo> {
        return when (val response = dataSource.getBadges()) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, BadgeInfo::class.java))
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun changeRepresentativeBadge(badgeIdx: Int): Result<RepresentativeBadge> {
        return when(val response = dataSource.changeRepresentativeBadge(badgeIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, RepresentativeBadge::class.java))
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getMonthBadge(): Result<MonthBadgeInfoDTO> {
        return when(val response = dataSource.getMonthBadge()){
            is Result.Success -> {
                LogUtils.d("getMonthBadge", response.toString())
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, MonthBadgeInfoDTO::class.java))
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}