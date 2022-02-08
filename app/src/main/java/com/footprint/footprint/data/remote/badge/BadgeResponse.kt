package com.footprint.footprint.data.remote.badge

data class GetBadgeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: BadgeResponse,
)

data class ChangeRepresentativeBadgeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: BadgeInfo,
)

data class BadgeResponse(
    val repBadgeInfo: BadgeInfo,
    val badgeList: List<BadgeInfo>
)

data class BadgeInfo(
    val badgeIdx: Int,
    val badgeName: String,
    val badgeUrl: String,
    val badgeDate: String,
    val badgeOrder: String
)