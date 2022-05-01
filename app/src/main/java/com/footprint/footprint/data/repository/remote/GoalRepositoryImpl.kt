package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.GoalRemoteDataSource
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.mapper.GoalMapper
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.model.UpdateGoal
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class GoalRepositoryImpl(private val dataSource: GoalRemoteDataSource): GoalRepository {
    override suspend fun getThisMonthGoal(): Result<Goal> {

        return when (val response = dataSource.getThisMonthGoal()) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Goal::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getNextMonthGoal(): Result<Goal> {
        return when(val response =  dataSource.getNextMonthGoal()) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Goal::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }

            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun updateGoal(month: String, goal: UpdateGoal): Result<Goal> {
        val encryptedGoal = NetworkUtils.encrypt(goal)
        val requestBody: RequestBody = encryptedGoal.toRequestBody("application/json".toMediaTypeOrNull())

        return when (val response = dataSource.updateGoal(requestBody)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(GoalMapper.mapperToGoal(month, goal))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}