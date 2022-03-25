package com.footprint.footprint.data.remote.achieve

import retrofit2.*
import retrofit2.http.GET

interface AchieveRetrofitInterface {
//    //일별 정보 조회 API
//    @GET("users/today")
//    fun getToday() : Call<TodayResponse>
//
//    //월별 정보 조회 API
//    @GET("users/tmonth")
//    fun getTMonth() : Call<TMonthResponse>

    // 마이페이지 통계 API
    @GET("users/infos")
    fun getInfoDetail(): Call<AchieveDetailResponse>
}