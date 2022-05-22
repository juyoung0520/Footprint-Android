package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.AchieveService
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.utils.LogUtils

class AchieveRemoteDataSourceImpl (private val api: AchieveService): BaseRepository(), AchieveRemoteDataSource{
    override suspend fun getToday(): Result<BaseResponse> {
        LogUtils.d("AchieveRemoteDataSourceImpl", "getToday")
        return safeApiCall() { api.getToday().body()!! }
    }

    override suspend fun getTmonth(): Result<BaseResponse> {
        LogUtils.d("AchieveRemoteDataSourceImpl", "getTmonth")
        return safeApiCall() { api.getTMonth().body()!! }
    }
    override suspend fun getUserInfo(): Result<BaseResponse> {
        return safeApiCall() { api.getInfoDetail().body()!! }
    }

}