package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.RequestBody
import retrofit2.http.Body

interface CourseRemoteDataSource {
    suspend fun getCourses(@Body bounds: RequestBody): Result<BaseResponse>
    suspend fun markCourse(courseIdx: Int) : Result<BaseResponse>
    suspend fun getCourseInfo(courseIdx: Int) : Result<BaseResponse>

    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
    suspend fun getMarkedCourses(): Result<BaseResponse>
    suspend fun getMyRecommendedCourses(): Result<BaseResponse>
}