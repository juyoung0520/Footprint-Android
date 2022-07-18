package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.mapper.UserMapper
import com.footprint.footprint.data.datasource.remote.UserRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.data.dto.UserDTO
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.model.InitUserModel
import com.footprint.footprint.domain.repository.UserRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class UserRepositoryImpl(private val dataSource: UserRemoteDataSource): UserRepository {

    override suspend fun registerUser(initUserModel: InitUserModel): Result<BaseResponse> {

        val encryptedData = NetworkUtils.encrypt(initUserModel)
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

    override suspend fun updateUser(myInfoUserModel: MyInfoUserModel): Result<BaseResponse> {

        val encryptedData = NetworkUtils.encrypt(myInfoUserModel)
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

    override suspend fun getUser(): Result<SimpleUserModel> {
        return when(val response = dataSource.getUser()){
            is Result.Success -> {
                if (response.value.isSuccess){
                    val user = NetworkUtils.decrypt(response.value.result, UserDTO::class.java)
                    val userInfo = UserMapper.mapperToSimpleUser(user)
                    Result.Success(userInfo)
                }
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getMyInfoUser(): Result<MyInfoUserModel> {
        return when(val response = dataSource.getUser()){
            is Result.Success -> {
                if (response.value.isSuccess){
                    val user = NetworkUtils.decrypt(response.value.result, UserDTO::class.java)
                    val userInfo = UserMapper.mapperToMyInfoUser(user)
                    Result.Success(userInfo)
                }
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}