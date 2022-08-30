package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result

interface CourseRemoteDataSource {
    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
}