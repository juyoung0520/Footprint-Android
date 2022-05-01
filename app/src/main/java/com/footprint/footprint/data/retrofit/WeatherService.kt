package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WeatherService {
    //날씨 API
    @POST("weather")
    suspend fun getWeather(@Body location: RequestBody) : Response<BaseResponse>
}