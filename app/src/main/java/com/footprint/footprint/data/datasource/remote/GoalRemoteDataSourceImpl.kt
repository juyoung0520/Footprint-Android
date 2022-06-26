package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.GoalService
import okhttp3.RequestBody

class GoalRemoteDataSourceImpl(private val api: GoalService): BaseRepository(), GoalRemoteDataSource {
    override suspend fun getThisMonthGoal(): Result<BaseResponse> {
        return safeApiCall2() { api.getThisMonthGoal() }
    }

    override suspend fun getNextMonthGoal(): Result<BaseResponse> {
        return safeApiCall2() { api.getNextMonthGoal() }
    }

    override suspend fun updateGoal(updateGoal: RequestBody): Result<BaseResponse> {
        return safeApiCall { api.updateGoal(updateGoal).body()!! }
    }
}