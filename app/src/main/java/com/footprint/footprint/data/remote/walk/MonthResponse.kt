package com.footprint.footprint.data.remote.walk
import com.google.gson.annotations.SerializedName

data class DayResult(
    @SerializedName("day")val day: Int,
    @SerializedName("walkCount")val walkCount: Int
)

data class MonthResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)
