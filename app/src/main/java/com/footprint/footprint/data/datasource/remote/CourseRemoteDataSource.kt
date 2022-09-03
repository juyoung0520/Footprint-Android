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
    //산책 코스 저장
    suspend fun saveCourse(request: RequestBody): Result<BaseResponse>
    //산책 코스 저장을 위한 상세 정보 조회
    suspend fun getWalkDetailForMakeCourse(walkNumber: Int): Result<BaseResponse>
    //나의 모든 코스(산책) 목록 조회
    suspend fun getSelfCourseLise(): Result<BaseResponse>
}