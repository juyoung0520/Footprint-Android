package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.UserService
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.getJwt
import okhttp3.RequestBody

class UserRemoteDataSourceImpl(private val api: UserService): BaseRepository(), UserRemoteDataSource {
    override suspend fun registerUser(userModel: RequestBody): Result<BaseResponse> {
        LogUtils.d("UserRemoteDataSourceImpl", "registerUser")
        return safeApiCall() { api.registerUser(userModel).body()!! }
    }

    override suspend fun updateUser(myInfoUserModel: RequestBody): Result<BaseResponse> {
        LogUtils.d("UserRemoteDataSourceImpl", "updateUser")
        return safeApiCall() { api.updateUser(myInfoUserModel).body()!! }
    }

    override suspend fun getUser(): Result<BaseResponse> {
        LogUtils.d("UserRemoteDataSourceImpl", "getUser jwt: ${getJwt()}")
        return safeApiCall() { api.getUser().body()!! }
    }
}