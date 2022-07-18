package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName


data class MonthBadgeInfoDTO(
    @SerializedName("badgeIdx") val badgeIdx: Int,
    @SerializedName("badgeName") val badgeName: String,
    @SerializedName("badgeUrl") val badgeUrl: String,
    @SerializedName("badgeDate")  val badgeDate: String,
)