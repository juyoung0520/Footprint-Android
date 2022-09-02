package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result

interface CourseRepository {
    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
    suspend fun getMarkedCourses(): Result<List<CourseDTO>>
    suspend fun getMyRecommendedCourses(): Result<List<CourseDTO>>
}