package com.footprint.footprint.data.remote.weather

import com.footprint.footprint.utils.getRetrofit

data class WeatherResponse(val response: RESPONSE)
data class RESPONSE(val header: HEADER, val body: BODY)
data class HEADER(val resultCode: Int, val resultMsg: String)
data class BODY(val dataType: String, val items: ITEMS, val totalCount: Int)
data class ITEMS(val item: List<ITEM>)

// category : 자료 구분 코드, fcstDate : 예측 날짜, fcstTime : 예측 시간, fcstValue : 예보 값
data class ITEM(
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String
)
