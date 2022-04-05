package com.footprint.footprint.data.remote.badge

import com.google.gson.annotations.SerializedName

data class BadgeInfo(
    val badgeIdx: Int,
    val badgeName: String,
    val badgeUrl: String,
    val badgeDate: String,
    val badgeOrder: String
)

/*이달의 뱃지 조회 Response*/
data class MonthBadgeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)