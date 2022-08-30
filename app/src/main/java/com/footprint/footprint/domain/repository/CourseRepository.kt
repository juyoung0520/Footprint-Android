package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result

interface CourseRepository {
    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
}