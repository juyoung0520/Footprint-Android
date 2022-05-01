package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.RequestBody
import retrofit2.http.Body

interface AuthRemoteDataSource {
    suspend fun autoLogin(): Result<BaseResponse>
    suspend fun login(@Body socialUserData: RequestBody): Result<BaseResponse>
    suspend fun unregister(): Result<BaseResponse>
}