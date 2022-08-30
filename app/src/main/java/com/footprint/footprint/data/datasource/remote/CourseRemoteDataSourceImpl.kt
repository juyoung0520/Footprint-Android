package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.CourseService

class CourseRemoteDataSourceImpl(private val api: CourseService): BaseRepository(), CourseRemoteDataSource {
    override suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse> {
        return safeApiCall { api.evaluateCourse(courseIdx, evaluate).body()!! }
    }
}