package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.CourseRemoteDataSource
import com.footprint.footprint.data.datasource.remote.WeatherRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.repository.CourseRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class CourseRepositoryImpl(private val dataSource: CourseRemoteDataSource): CourseRepository {
    override suspend fun getCourses(bounds: BoundsModel): Result<List<CourseDTO>> {

        val encryptedData = NetworkUtils.encrypt(bounds)
        LogUtils.d("COURSE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.getCourses(data)){
            is Result.Success -> {
                if (response.value.isSuccess){
                    val type = object : TypeToken<List<CourseDTO>>() {}.type
                    Result.Success(NetworkUtils.decrypt(response.value.result, type))
                }
                else{
                    Result.GenericError(response.value.code, "")
                }
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun markCourse(courseIdx: Int): Result<BaseResponse> {
        return when(val response = dataSource.markCourse(courseIdx)){
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

    override suspend fun getCourseInfo(courseIdx: Int): Result<CourseInfoDTO> {
        return when(val response = dataSource.getCourseInfo(courseIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(
                        NetworkUtils.decrypt(
                            response.value.result,
                            CourseInfoDTO::class.java
                        )
                    )
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

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