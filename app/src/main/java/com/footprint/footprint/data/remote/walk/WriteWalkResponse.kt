package com.footprint.footprint.data.remote.walk

data class WriteWalkResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<AcquiredBadge>?
)

data class AcquiredBadge(
    val badgeIdx: Int,
    val badgeName: String,
    val badgeUrl: String
)
