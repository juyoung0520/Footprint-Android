package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.CourseRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.CourseRepository
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken

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

    override suspend fun getMarkedCourses(): Result<List<CourseDTO>> {
        return when(val response = dataSource.getMarkedCourses()) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val itemType = object : TypeToken<List<CourseDTO>>() {}.type
                    NetworkUtils.decrypt(response.value.result, itemType)
                }
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getMyRecommendedCourses(): Result<List<CourseDTO>> {
        return when(val response = dataSource.getMarkedCourses()) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val itemType = object : TypeToken<List<CourseDTO>>() {}.type
                    NetworkUtils.decrypt(response.value.result, itemType)
                }
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }
}