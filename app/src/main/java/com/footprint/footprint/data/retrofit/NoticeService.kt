package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NoticeService {

    // 공지사항 목록 조회 API
    @GET("notice/list")
    suspend fun getNoticeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse>

    // 공지사항 조회 API
    @GET("notice")
    suspend fun getNotice(
        @Query("idx") idx: Int,
    ): Response<BaseResponse>

    // 새로운 공지사항 존재 여부 조회 API
    @GET("notice/new")
    suspend fun getNewNotice(): Response<BaseResponse>

    // 필수 공지사항 조회 API
    @POST("notice/key")
    suspend fun getKeyNotice(@Body checkedKeyNoticeIdxList: RequestBody): Response<BaseResponse>
}