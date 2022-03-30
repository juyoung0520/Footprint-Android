package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

data class DayWalkDTO (
    @SerializedName("userDateWalk")val walk: UserDateWalkDTO,
    @SerializedName("hashtag")val hashtag: List<String>,
)

data class UserDateWalkDTO(
    @SerializedName("walkIdx")val walkIdx: Int,
    @SerializedName("startTime")val startTime: String,
    @SerializedName("endTime")val endTime: String,
    @SerializedName("pathImageUrl")val pathImageUrl: String,
)
