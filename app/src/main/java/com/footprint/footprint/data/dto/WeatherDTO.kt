package com.footprint.footprint.data.dto
import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    @SerializedName("temperature") val temperature: String, //기온 정보(접근 오류 시 -)
    @SerializedName("weather") val weather: String          //날씨 정보(접근 오류 시 -)
)

