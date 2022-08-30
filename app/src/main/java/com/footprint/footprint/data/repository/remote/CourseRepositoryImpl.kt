package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.CourseRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.CourseRepository
import com.footprint.footprint.utils.NetworkUtils

class CourseRepositoryImpl(private val dataSource: CourseRemoteDataSource): CourseRepository {
    override suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse> {
        return when(val response = dataSource.evaluateCourse(courseIdx, evaluate)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    response
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }
}