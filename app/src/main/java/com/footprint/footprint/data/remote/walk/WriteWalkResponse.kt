package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.remote.badge.BadgeInfo

data class WriteWalkResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<BadgeInfo>?
)
