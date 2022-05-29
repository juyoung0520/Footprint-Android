package com.footprint.footprint.domain.model

data class GetWalkEntity(
    val walkIdx: Int,
    val walkDate: String,
    val walkStartTime: String,
    val walkEndTime: String,
    val walkTime: String,
    val calorie: Int,
    val distance: Double,
    val footCount: Int,
    val pathImageUrl: String
)