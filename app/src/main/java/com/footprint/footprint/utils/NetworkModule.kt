package com.footprint.footprint.utils

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*Retrofit 사용을 편리하게 하기 위한 NetworkModule.kt*/

//날씨 API
const val WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
val gson = GsonBuilder().setLenient().create()

class NetworkModule{
    companion object{
        var apiInstance: Retrofit? = null

        fun getWeatherRetrofit(): Retrofit? {
            if(apiInstance == null){
                apiInstance = Retrofit.Builder()
                    .baseUrl(WEATHER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

            return apiInstance
        }
    }
}


