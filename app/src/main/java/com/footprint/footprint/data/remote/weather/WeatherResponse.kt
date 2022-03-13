package com.footprint.footprint.data.remote.weather
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)

data class Weather(
    @SerializedName("temperature") val temperature: String, //기온 정보(접근 오류 시 -)
    @SerializedName("weather") val weather: String          //날씨 정보(접근 오류 시 -)
)

