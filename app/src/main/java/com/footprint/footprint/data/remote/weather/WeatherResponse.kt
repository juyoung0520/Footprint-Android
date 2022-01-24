package com.footprint.footprint.data.remote.weather
import com.google.gson.annotations.SerializedName

data class WeatherResponse(@SerializedName("response") val response: RESPONSE)
data class RESPONSE(
    @SerializedName("header") val header: HEADER,
    @SerializedName("body") val body: BODY
)

data class HEADER(
    @SerializedName("resultCode") val resultCode: Int,
    @SerializedName("resultMsg") val resultMsg: String
)

data class BODY(
    @SerializedName("dataType") val dataType: String,
    @SerializedName("items") val items: ITEMS,
    @SerializedName("totalCount") val totalCount: Int
)

data class ITEMS(@SerializedName("item") val item: List<ITEM>)

// category : 자료 구분 코드, fcstDate : 예측 날짜, fcstTime : 예측 시간, fcstValue : 예보 값
data class ITEM(
    @SerializedName("baseDate") val baseDate: String,
    @SerializedName("baseTime") val baseTime: String,
    @SerializedName("category") val category: String,
    @SerializedName("fcstDate") val fcstDate: String,
    @SerializedName("fcstTime") val fcstTime: String,
    @SerializedName("fcstValue") val fcstValue: String
)
