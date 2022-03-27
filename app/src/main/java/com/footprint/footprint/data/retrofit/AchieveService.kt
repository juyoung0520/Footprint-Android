package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Response
import retrofit2.http.GET

interface AchieveService {
    //일별 정보 조회 API
    @GET("users/today")
    suspend fun getToday() : Response<BaseResponse>

    //월별 정보 조회 API
    @GET("users/tmonth")
    suspend fun getTMonth() : Response<BaseResponse>

    // 마이페이지 통계 API
    @GET("users/infos")
    suspend fun getInfoDetail(): Response<BaseResponse>
}