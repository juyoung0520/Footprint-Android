package com.footprint.footprint.data.remote.walk

import com.google.gson.annotations.SerializedName

data class UserDateWalk(
    @SerializedName("walkIdx")val walkIdx: Int,
    @SerializedName("startTime")val startTime: String,
    @SerializedName("endTime")val endTime: String,
    @SerializedName("pathImageUrl")val pathImageUrl: String,
)

data class DayWalkResult(
    @SerializedName("userDateWalk")val walk: UserDateWalk,
    @SerializedName("hashtag")val hashtag: List<String>,
)

// 날짜당 산책리스트
data class DayWalksResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: List<DayWalkResult>
)
