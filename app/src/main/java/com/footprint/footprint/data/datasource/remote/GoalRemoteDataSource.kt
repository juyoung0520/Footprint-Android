package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.RequestBody


interface GoalRemoteDataSource {
    suspend fun getThisMonthGoal(): Result<BaseResponse>
    suspend fun getNextMonthGoal(): Result<BaseResponse>
    suspend fun updateGoal(updateGoal: RequestBody): Result<BaseResponse>
}