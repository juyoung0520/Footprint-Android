package com.footprint.footprint.data.remote.walk

import com.google.gson.annotations.SerializedName

data class WalkDateResult(
    @SerializedName("walkAt")val walkAt: String,
    @SerializedName("walks")val walks: List<DayWalkResult>,
)

// 태그 검색 결과
data class TagWalkDatesResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: List<WalkDateResult>?
)
