package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.model.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.retrofit.GoalService
import com.footprint.footprint.utils.LogUtils

class GoalRemoteDataSourceImpl(private val api: GoalService): BaseRepository(), GoalRemoteDataSource {
    override suspend fun getThisMonthGoal(): Result<BaseResponse> {
        LogUtils.d("GoalRemoteDataSourceImpl", "getThisMonthGoal")
        return safeApiCall() { api.getThisMonthGoal().body()!! }
    }
}