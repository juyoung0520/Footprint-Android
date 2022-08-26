package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.model.LocationModel
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseRepository {
    suspend fun getCourses(bounds: BoundsModel): Result<List<CourseDTO>>
    suspend fun markCourse(courseIdx: Int) : Result<BaseResponse>
    suspend fun getCourseInfo(courseIdx: Int) : Result<CourseInfoDTO>

    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
    suspend fun getMarkedCourses(): Result<List<CourseDTO>>
    suspend fun getMyRecommendedCourses(): Result<List<CourseDTO>>
}