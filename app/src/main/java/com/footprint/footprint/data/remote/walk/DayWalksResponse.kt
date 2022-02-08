package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.model.WalkModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue

data class DayWalkResult(
    @SerializedName("userDateWalk")val walk: WalkModel,
    @SerializedName("hashtag")val hashtag: List<String>,
)

data class DayWalksResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: List<DayWalkResult>
)
