package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.UserMapper
import com.footprint.footprint.data.datasource.remote.UserRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.*
import com.footprint.footprint.data.dto.User
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.model.UserInfoModel
import com.footprint.footprint.domain.model.UserModel
import com.footprint.footprint.domain.repository.UserRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class UserRepositoryImpl(private val dataSource: UserRemoteDataSource): UserRepository {

    override suspend fun registerUser(userModel: UserModel): Result<BaseResponse> {

        val encryptedData = NetworkUtils.encrypt(userModel)
        LogUtils.d("REGISTER/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.registerUser(data)){
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

    override suspend fun updateUser(simpleUserModel: SimpleUserModel): Result<BaseResponse> {

        val encryptedData = NetworkUtils.encrypt(simpleUserModel)
        LogUtils.d("UPDATE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.updateUser(data)){
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

    override suspend fun getUser(): Result<User> {
        return when(val response = dataSource.getUser()){
            is Result.Success -> {
                if (response.value.isSuccess){
                    val user = NetworkUtils.decrypt(response.value.result, User::class.java)
                    //val userInfo = UserMapper.mapperToUserInfo(user)
                    Result.Success(user)
                }
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}