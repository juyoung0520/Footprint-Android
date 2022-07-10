package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.RequestBody

interface WalkRemoteDataSource {
    suspend fun getWalkByIdx(walkIdx: Int): Result<BaseResponse>
    suspend fun deleteWalk(walkIdx: Int): Result<BaseResponse>
    suspend fun saveWalk(request: RequestBody): Result<BaseResponse>
    suspend fun getMonthWalks(year: Int, month: Int): Result<BaseResponse>
    suspend fun getDayWalks(date: String): Result<BaseResponse>
    suspend fun getTagWalks(tag: String): Result<BaseResponse>
}