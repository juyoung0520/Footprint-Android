package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CourseService {

    //코스 조회 API
    @POST("courses/list")
    suspend fun getCourses(@Body bounds: RequestBody) : Response<BaseResponse>

    //코스 북마크 API
    @PATCH("courses/mark/{courseIdx}")
    suspend fun markCourse(@Path("courseIdx")courseIdx: Int) : Response<BaseResponse>

    //코스 상세정보 API
    @GET("courses/{courseIdx}/infos")
    suspend fun getCourseInfo(@Path("courseIdx")courseIdx: Int) : Response<BaseResponse>

    @PATCH("courses/like/{courseIdx}/{evaluate}")
    suspend fun evaluateCourse(@Path("courseIdx") courseIdx: Int, @Path("evaluate") evaluate: Int): Response<BaseResponse>

    @GET("courses/list/mark")
    suspend fun getMarkedCourses(): Response<BaseResponse>

    @GET("courses/list/recommend")
    suspend fun getMyRecommendedCourses(): Response<BaseResponse>
}