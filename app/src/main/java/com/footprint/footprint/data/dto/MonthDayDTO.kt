package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

data class MonthDayDTO(
    @SerializedName("day")val day: Int,
    @SerializedName("walkCount")val walkCount: Int
)
