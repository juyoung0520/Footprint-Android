package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NoticeService {

    // 공지사항 목록 조회 API
    @GET("notices/list")
    suspend fun getNoticeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse>

    // 공지사항 조회 API
    @GET("notices")
    suspend fun getNotice(
        @Query("idx") idx: Int,
    ): Response<BaseResponse>

    // 새로운 공지사항 존재 여부 조회 API
    @GET("notices/new")
    suspend fun getNewNotice(): Response<BaseResponse>

    // 필수 공지사항 조회 API
    @POST("notices/key")
    suspend fun getKeyNotice(@Body checkedKeyNoticeIdxList: RequestBody): Response<BaseResponse>

    // 버전 확인 API
    @GET("/notices/version/{userVersion}")
    suspend fun getVersion(@Path("userVersion") userVersion: String): Response<BaseResponse>
}