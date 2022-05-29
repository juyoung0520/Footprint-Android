package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.WalkService
import okhttp3.RequestBody

class WalkRemoteDataSourceImpl(private val api: WalkService): BaseRepository(), WalkRemoteDataSource {
    override suspend fun getWalkByIdx(walkIdx: Int): Result<BaseResponse> {
        return safeApiCall { api.getWalkByIdx(walkIdx).body()!! }
    }

    override suspend fun deleteWalk(walkIdx: Int): Result<BaseResponse> {
        return safeApiCall { api.deleteWalk(walkIdx).body()!! }
    }

    override suspend fun saveWalk(request: RequestBody): Result<BaseResponse> {
        return safeApiCall { api.saveWalk(request).body()!! }
    }

    override suspend fun getMonthWalks(year: Int, month: Int): Result<BaseResponse> {
        return safeApiCall { api.getMonthWalks(year, month).body()!! }
    }

    override suspend fun getDayWalks(date: String): Result<BaseResponse> {
        return safeApiCall { api.getDayWalks(date).body()!! }
    }

    override suspend fun getTagWalks(tag: String): Result<BaseResponse> {
        return  safeApiCall { api.getTagWalks(tag).body()!! }
    }
}