package com.footprint.footprint.data.remote.weather

import retrofit2.Call
import retrofit2.http.*

interface WeatherRetrofitInterface {
    @GET("weather")
    fun getWeather(@Query("nx") nx: String, @Query("ny") ny: String) : Call <WeatherResponse>
}
