package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.GoalRemoteDataSource
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.NetworkUtils
import com.footprint.footprint.utils.RemoteErrorEmitter
import java.io.IOException

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
}