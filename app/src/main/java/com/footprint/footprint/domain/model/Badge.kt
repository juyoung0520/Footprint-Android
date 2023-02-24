package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName

data class BadgeInfo(
    @SerializedName("repBadgeDateInfo") val repBadgeInfo: RepresentativeBadge = RepresentativeBadge(),
    val badgeList: List<Badge> = arrayListOf()
)

data class Badge(
    val badgeIdx: Int = 0,
    val badgeName: String = "",
    val badgeUrl: String = "",
    val badgeDate: String = "",
    val badgeOrder: String = ""
)

data class RepresentativeBadge(
    val badgeDate: String = "",
    @SerializedName("badgeInfo") val repBadgeInfo: RepresentativeBadgeInfo = RepresentativeBadgeInfo()
)

data class RepresentativeBadgeInfo(
    val badgeUrl: String = "",
    val badgeIdx: Int = 0,
    val badgeName: String = ""
)
