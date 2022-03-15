package com.footprint.footprint.data.remote.weather

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface WeatherRetrofitInterface {
    @POST("weather")
    fun getWeather(@Body location: RequestBody) : Call <WeatherResponse>
}
