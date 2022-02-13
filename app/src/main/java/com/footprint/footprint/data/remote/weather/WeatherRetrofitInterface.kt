package com.footprint.footprint.data.remote.weather

import retrofit2.Call
import retrofit2.http.*

interface WeatherRetrofitInterface {
    @GET("getVilageFcst")
    fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") num_of_rows: Int,   // 한 페이지 경과 수
        @Query("pageNo") page_no: Int,          // 페이지 번호
        @Query("dataType") data_type: String,          // 데이터 타입
        @Query("base_date") base_date: String,  // 발표 일자
        @Query("base_time") base_time: String,  // 발표 시각
        @Query("nx") nx: String,                // 예보지점 X 좌표
        @Query("ny") ny: String,                // 예보지점 Y 좌표
    )
        : Call<WeatherResponse>
}
