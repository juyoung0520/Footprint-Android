package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.AuthService
import com.footprint.footprint.utils.LogUtils
import okhttp3.RequestBody

class AuthRemoteDataSourceImpl(private val api: AuthService): BaseRepository(), AuthRemoteDataSource {
    override suspend fun autoLogin(): Result<BaseResponse> {
        LogUtils.d("AuthRemoteDataSourceImpl", "autoLogin")
        return safeApiCall() { api.autoLogin().body()!! }
    }

    override suspend fun login(socialUserData: RequestBody): Result<BaseResponse> {
        LogUtils.d("AuthRemoteDataSourceImpl", "login")
        return safeApiCall() { api.login(socialUserData).body()!! }
    }

    override suspend fun unregister(): Result<BaseResponse> {
        LogUtils.d("AuthRemoteDataSourceImpl", "unregisterlogin")
        return safeApiCall() { api.unregister().body()!! }
    }


}