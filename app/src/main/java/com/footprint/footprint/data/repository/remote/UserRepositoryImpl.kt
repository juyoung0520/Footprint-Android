package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.UserRemoteDataSource
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SimpleUser
import com.footprint.footprint.domain.repository.UserRepository
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepositoryImpl(private val dataSource: UserRemoteDataSource): UserRepository {
    override suspend fun updateUser(user: SimpleUser): Result<SimpleUser> {
        val encryptedData = NetworkUtils.encrypt(user)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when (val response = dataSource.updateUser(data)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(user)
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}