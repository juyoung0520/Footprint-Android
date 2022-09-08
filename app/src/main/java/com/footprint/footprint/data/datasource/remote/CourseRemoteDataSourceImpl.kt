package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.CourseService
import com.footprint.footprint.utils.LogUtils
import okhttp3.RequestBody

class CourseRemoteDataSourceImpl(private val api: CourseService): BaseRepository(), CourseRemoteDataSource {
    override suspend fun saveCourse(request: RequestBody): Result<BaseResponse> {
        return safeApiCall2 { api.saveCourse(request) }
    }

    override suspend fun getWalkDetailForMakeCourse(walkNumber: Int): Result<BaseResponse> {
        return safeApiCall2 { api.getWalkDetailForMakeCourse(walkNumber) }
    }

    override suspend fun getSelfCourseLise(): Result<BaseResponse> {
        return safeApiCall2 { api.getSelfCourseList() }
    }

    override suspend fun deleteCourse(courseIdx: Int): Result<BaseResponse> {
        return safeApiCall2 { api.deleteCourse(courseIdx) }
    }

    override suspend fun updateCourse(request: RequestBody): Result<BaseResponse> {
        return safeApiCall2 { api.updateCourse(request) }
    }

    override suspend fun getCourses(bounds: RequestBody): Result<BaseResponse> {
        LogUtils.d("CourseRemoteDataSourceImpl", "getCourses")
        return safeApiCall() { api.getCourses(bounds).body()!! }
    }

    override suspend fun markCourse(courseIdx: Int): Result<BaseResponse> {
        LogUtils.d("CourseRemoteDataSourceImpl", "markCourse")
        return safeApiCall() { api.markCourse(courseIdx).body()!! }
    }

    override suspend fun getCourseInfo(courseIdx: Int): Result<BaseResponse> {
        LogUtils.d("CourseRemoteDataSourceImpl", "getCourseInfo")
        return safeApiCall() { api.getCourseInfo(courseIdx).body()!! }
    }

   override suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse> {
        return safeApiCall { api.evaluateCourse(courseIdx, evaluate).body()!! }
    }

    override suspend fun getMarkedCourses(): Result<BaseResponse> {
        return safeApiCall { api.getMarkedCourses().body()!! }
    }

    override suspend fun getMyRecommendedCourses(): Result<BaseResponse> {
        return safeApiCall { api.getMyRecommendedCourses().body()!! }
    }
}