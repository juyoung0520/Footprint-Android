package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CourseService {
    @PATCH("courses/like/{courseIdx}/{evaluate}")
    suspend fun evaluateCourse(@Path("courseIdx") courseIdx: Int, @Path("evaluate") evaluate: Int): Response<BaseResponse>

    @GET("courses/list/mark")
    suspend fun getMarkedCourses(): Response<BaseResponse>

    @GET("courses/list/recommend")
    suspend fun getMyRecommendedCourses(): Response<BaseResponse>
}