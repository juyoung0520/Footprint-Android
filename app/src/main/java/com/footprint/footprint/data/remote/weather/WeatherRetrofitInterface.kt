package com.footprint.footprint.data.remote.weather

import com.footprint.footprint.utils.getRetrofit
import retrofit2.Call
import retrofit2.http.*

interface WeatherRetrofitInterface {

    // getUltraSrtFcst 초단기 예보 조회
    @GET("getVilageFcst?serviceKey=V%2B1MlI%2BZLP5u0ofnxvGVJTDzfOdPeQJLx1HCCC93OAP%2FMcupiIw1U%2F%2B7E1OUAeVqcFEkqwgkdSRHiMReIf8zVA%3D%3D")
    fun GetWeather(
        @Query("numOfRows") num_of_rows: Int,   // 한 페이지 경과 수
        @Query("pageNo") page_no: Int,          // 페이지 번호
        @Query("base_date") base_date: String,  // 발표 일자
        @Query("base_time") base_time: String,  // 발표 시각
        @Query("nx") nx: String,                // 예보지점 X 좌표
        @Query("ny") ny: String)                // 예보지점 Y 좌표
        : Call<WeatherResponse>
}

object ApiObject {
    val retrofitService: WeatherRetrofitInterface by lazy {
        getRetrofit().create(WeatherRetrofitInterface::class.java)
    }

}