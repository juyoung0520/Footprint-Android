package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

/*이달의 뱃지 조회 Response*/
data class MonthBadgeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)