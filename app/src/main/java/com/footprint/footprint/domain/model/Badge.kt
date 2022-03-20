package com.footprint.footprint.domain.model

data class BadgeInfo(
    val repBadgeInfo: Badge = Badge(),
    val badgeList: List<Badge> = arrayListOf()
)

data class Badge(
    val badgeIdx: Int = 0,
    val badgeName: String = "",
    val badgeUrl: String = "",
    val badgeDate: String = "",
    val badgeOrder: String = ""
)
