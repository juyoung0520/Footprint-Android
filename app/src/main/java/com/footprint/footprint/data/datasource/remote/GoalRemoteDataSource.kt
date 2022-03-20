package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.model.BaseResponse
import com.footprint.footprint.data.model.Result


interface GoalRemoteDataSource {
    suspend fun getThisMonthGoal(): Result<BaseResponse>
}