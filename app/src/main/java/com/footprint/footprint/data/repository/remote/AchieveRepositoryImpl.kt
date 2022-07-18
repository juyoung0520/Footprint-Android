package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.AchieveRemoteDataSource
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.repository.AchieveRepository
import com.footprint.footprint.utils.NetworkUtils

class AchieveRepositoryImpl(private val dataSource: AchieveRemoteDataSource): AchieveRepository {
    override suspend fun getToday(): Result<TodayDTO> {
        return when(val response = dataSource.getToday()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, TodayDTO::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getTmonth(): Result<TMonthDTO> {
        return when(val response = dataSource.getTmonth()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, TMonthDTO::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
    override suspend fun getUserInfo(): Result<UserInfoDTO> {
        return when (val response = dataSource.getUserInfo()) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, UserInfoDTO::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}