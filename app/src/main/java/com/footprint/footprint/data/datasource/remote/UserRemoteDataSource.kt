package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import okhttp3.RequestBody
import retrofit2.http.Body

interface UserRemoteDataSource {
    suspend fun registerUser(@Body initUserModel: RequestBody): Result<BaseResponse>
    suspend fun updateUser(@Body myInfoUserModel: RequestBody): Result<BaseResponse>
    suspend fun getUser(): Result<BaseResponse>
}