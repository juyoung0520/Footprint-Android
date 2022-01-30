package com.footprint.footprint.data.remote.badge

data class BadgeResponse(
    val badges: List<Badge>,
    var representativeBadge: Badge
)

data class Badge(
    val name: String,
    val img: Int,
    val order: Int
)
