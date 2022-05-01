package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result

interface AchieveRemoteDataSource {
    suspend fun getToday(): Result<BaseResponse>
    suspend fun getTmonth(): Result<BaseResponse>
    suspend fun getInfoDetail(): Result<BaseResponse>
}