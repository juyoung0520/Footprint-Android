package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.AuthRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.dto.Login
import com.footprint.footprint.domain.model.SocialUserModel
import com.footprint.footprint.domain.repository.AuthRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepositoryImpl(private val dataSource: AuthRemoteDataSource): AuthRepository {

    override suspend fun autoLogin(): Result<Login> {
        return when(val response = dataSource.autoLogin()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Login::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun login(socialUserData: SocialUserModel): Result<Login> {

        val encryptedData = NetworkUtils.encrypt(socialUserData)
        LogUtils.d("UPDATE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.login(data)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Login::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun unregister(): Result<BaseResponse> {
        return when(val response = dataSource.unregister()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(response.value)
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

}