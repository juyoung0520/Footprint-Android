package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

/*이달의 뱃지 조회 Response*/
/*
data class MonthBadgeDTO(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: BadgeInfo
)
*/

data class MonthBadgeInfoDTO(
    @SerializedName("badgeIdx") val badgeIdx: Int,
    @SerializedName("badgeName") val badgeName: String,
    @SerializedName("badgeUrl") val badgeUrl: String,
    @SerializedName("badgeDate")  val badgeDate: String,
)