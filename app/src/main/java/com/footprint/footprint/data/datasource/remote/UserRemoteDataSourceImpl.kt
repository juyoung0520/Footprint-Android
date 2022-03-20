package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.UserService
import okhttp3.RequestBody

class UserRemoteDataSourceImpl(private val api: UserService): BaseRepository(), UserRemoteDataSource {
    override suspend fun updateUser(user: RequestBody): Result<BaseResponse> {
        return safeApiCall { api.updateUser(user).body()!! }
    }
}