package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.RequestBody

interface UserRemoteDataSource {
    suspend fun updateUser(user: RequestBody): Result<BaseResponse>
}