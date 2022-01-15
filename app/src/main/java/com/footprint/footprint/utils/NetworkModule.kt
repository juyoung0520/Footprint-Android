package com.footprint.footprint.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*Retrofit 사용을 편리하게 하기 위한 NetworkModule.kt*/
const val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit
}

