package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeService {

    // 공지사항 목록 조회 API
    @GET("notice")
    suspend fun getNoticeList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse>

    // 공지사항 조회 API
    @GET("notice")
    suspend fun getNotice(
        @Query("idx") idx: Int,
    ): Response<BaseResponse>
}