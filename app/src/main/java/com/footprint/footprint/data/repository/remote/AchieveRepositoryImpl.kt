package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.AchieveRemoteDataSource
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.domain.repository.AchieveRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils

class AchieveRepositoryImpl(private val dataSource: AchieveRemoteDataSource): AchieveRepository {
    override suspend fun getToday(): Result<Today> {
        return when(val response = dataSource.getToday()){
            is Result.Success -> {
                LogUtils.d("GET today", "resp: ${response.value.result}")
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Today::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getTmonth(): Result<TMonth> {
        return when(val response = dataSource.getTmonth()){
            is Result.Success -> {
                LogUtils.d("GET tmonth", "resp: ${response.value.result}")
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, TMonth::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}