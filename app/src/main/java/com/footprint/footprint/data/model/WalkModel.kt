package com.footprint.footprint.data.model

import com.google.gson.annotations.SerializedName

data class WalkModel(
    @SerializedName("walkIdx")var walkIdx: Int = 0,
    @SerializedName("startTime")var startAt: String = "",
    @SerializedName("endTime")var endAt: String = "",
    @SerializedName("pathImageUrl")var pathImg: String = "",
)
