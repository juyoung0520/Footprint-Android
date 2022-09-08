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

    //코스 저장
    @POST("/courses/recommend")
    suspend fun saveCourse(@Body request: RequestBody): Response<BaseResponse>

    //산책 코스를 저장하기 위해 필요한 산책 상세 정보를 조회하는 API
    @GET("/courses/path/{walkNumber}")
    suspend fun getWalkDetailForMakeCourse(@Path("walkNumber") walkNumber: Int): Response<BaseResponse>

    //나의 모든 코스(산책) 목록 조회
    @GET("/courses/list/self")
    suspend fun getSelfCourseList(): Response<BaseResponse>

    //코스 수정
    @PATCH("/courses/recommend")
    suspend fun updateCourse(@Body request: RequestBody): Response<BaseResponse>

    //코스 삭제
    @PATCH("/courses/recommend/{courseIdx}/status")
    suspend fun deleteCourse(@Path("courseIdx") courseIdx: Int): Response<BaseResponse>
}